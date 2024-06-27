package com.exigen.ie.constrainer.impl;

import java.util.LinkedList;
import java.util.ListIterator;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntArrayCards;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
/**
 * Title: Cardinality of Integer Expression in Integer Expression Array
 * Description: class
 * Copyright: 2002
 * Company: Exigen Group, Inc.
 * @author Sergej Vanskov
 * @version 1.0
 */

public class IntExpCardIntExp extends IntExpImpl {
  private IntVar                    _result;
  private IntExp                    _exp;
  private LinkedList                _cards;
  private IntExpCardIntExpObserver  _exp_observer;

  public IntExpCardIntExp (IntExpArray array, IntExp exp) throws Failure {
    super (exp.constrainer ());
    if(constrainer ().showInternalNames ()) {
      name ("C{" + array.name() + "/" + exp.name() + "}");
    }

    _exp = exp;

    _cards = new LinkedList ();

    int                           min = _exp.min ();
    int                           max = _exp.max ();
    IntExpCard                    card;
    IntArrayCards                 cards = array.cards ();
    IntExpCardCardinalityObserver observer;

    for (int i = min; i <= max; i++) {
      if (_exp.contains (i)) {
        card = cards.cardAt (i);
        observer = new IntExpCardCardinalityObserver (card);
        card.attachObserver (observer);
        _cards.add (new IntExpAndObserver (card, observer));
      }
    }

    _exp_observer = new IntExpCardIntExpObserver (_cards, _exp);
    _exp.attachObserver (_exp_observer);

    int trace = 0;
    _result = constrainer ().addIntVarTraceInternal(
      calc_min (),
      calc_max (),
      "IX{" + array.name () + "/" + exp.name () + "}",
      IntVar.DOMAIN_PLAIN,
      trace
    );
  }
  public void setMin (int min) throws com.exigen.ie.constrainer.Failure {
    _result.setMin (min);

    if (_cards.size () == 1) {
      ((IntExpCard)_cards.get (0)).setMin (min);
    }
  }
  public void setMax (int max) throws com.exigen.ie.constrainer.Failure {
    _result.setMax (max);

    if (_cards.size () == 1) {
      ((IntExpCard)_cards.get (0)).setMax (max);
    }
  }
  public int min () {
    return _result.min ();
  }
  public int max () {
    return _result.max ();
  }
  public int calc_min () {
    if (_cards.size () == 1) {
      return ((IntExpCard) ((IntExpAndObserver) _cards.getFirst ())._exp).min ();
    }
    ListIterator iterator = _cards.listIterator ();
    int l_min = Constrainer.INT_MAX;
    int e_min;

    while (iterator.hasNext ()) {
      e_min = (
        (IntExpCard)
          ((IntExpAndObserver)iterator.next ())._exp).min ();
      if (e_min < l_min) {
        l_min = e_min;
      }
    }
    return l_min;
  }
  public int calc_max () {
    if (_cards.size () == 1) {
      return ((IntExpCard) ((IntExpAndObserver) _cards.getFirst ())._exp).max ();
    }
    ListIterator iterator = _cards.listIterator ();
    int l_max = Constrainer.INT_MIN;
    int e_max;

    while (iterator.hasNext ()) {
      e_max = ((IntExpCard) ((IntExpAndObserver)iterator.next ())._exp).max ();
      if (e_max > l_max) {
        l_max = e_max;
      }
    }
    return l_max;
  }
  private class IntExpAndObserver {
    public IntExpAndObserver (IntExp exp, Observer observer) {
      _exp = exp;
      _observer = observer;
    }
    public IntExp   _exp;
    public Observer _observer;
  }

  private class IntExpCardIntExpObserver extends Observer {
    IntExp      _exp;
    LinkedList  _cards;

    public IntExpCardIntExpObserver (LinkedList cards, IntExp exp) {
      _cards = cards;
      _exp = exp;
    }
    public void updateMinMax () throws Failure {
      _result.setMin (calc_min ());
      _result.setMax (calc_max ());
    }
    public void update (Subject subject, EventOfInterest event) throws Failure {
      IntEvent  i_event = (IntEvent) event;
      switch (event.type ()) {
        case MIN:
          {
            int min = i_event.min ();
            IntExpAndObserver exp_and_observer;

            while (!_cards.isEmpty ()) {
              exp_and_observer = (IntExpAndObserver) _cards.getFirst ();
              if (((IntExpCard)exp_and_observer._exp).get_cardinality_value() < min) {
                exp_and_observer._exp.detachObserver (exp_and_observer._observer);
                _cards.removeFirst ();
              } else {
                break;
              }
            }
            updateMinMax ();
          }
          break;
        case MAX:
          {
            int max = i_event.max ();
            IntExpAndObserver exp_and_observer;

            while (!_cards.isEmpty ()) {
              exp_and_observer = (IntExpAndObserver) _cards.getLast ();
              if (((IntExpCard)exp_and_observer._exp).get_cardinality_value() > max) {
                exp_and_observer._exp.detachObserver (exp_and_observer._observer);
                _cards.removeLast ();
              } else {
                break;
              }
            }
            updateMinMax ();
          }
          break;
        case VALUE:
        case MINMAX:
          {
            int min = i_event.min ();
            IntExpAndObserver exp_and_observer;

            while (!_cards.isEmpty ()) {
              exp_and_observer = (IntExpAndObserver) _cards.getFirst ();
              if (((IntExpCard)exp_and_observer._exp).get_cardinality_value() < min) {
                exp_and_observer._exp.detachObserver (exp_and_observer._observer);
                _cards.removeFirst ();
              } else {
                break;
              }
            }

            int max = i_event.max ();

            while (!_cards.isEmpty ()) {
              exp_and_observer = (IntExpAndObserver) _cards.getLast ();
              if (((IntExpCard)exp_and_observer._exp).get_cardinality_value() > max) {
                exp_and_observer._exp.detachObserver (exp_and_observer._observer);
                _cards.removeLast ();
              } else {
                break;
              }
            }

            updateMinMax ();
          }
          break;
        case REMOVE:
          {
            for (int i = 0; i < i_event.numberOfRemoves (); i++ ) {
              int               rem = i_event.removed (i);
              IntExpAndObserver exp_and_observer;
              ListIterator      iterator = _cards.listIterator ();
              while (iterator.hasNext ()) {
                exp_and_observer = (IntExpAndObserver) iterator.next ();
                if ( ((IntExpCard)exp_and_observer._exp).get_cardinality_value () == rem) {
                  exp_and_observer._exp.detachObserver (exp_and_observer._observer);
                  _cards.remove (exp_and_observer);
                  break;
                }
              }
            }
            updateMinMax ();
          }
          break;
      }
    }
    public Object master () {
      return IntExpCardIntExp.this;
    }
    public int subscriberMask() {
      return MIN | MAX | VALUE | REMOVE;
    }
  }

  private class IntExpCardCardinalityObserver extends Observer {
    private IntExpCard  _exp;

    public IntExpCardCardinalityObserver (IntExpCard exp) {
      _exp = exp;
    }
    public void update (Subject subject, EventOfInterest event) throws Failure {
      IntEvent  i_event = (IntEvent) event;

      if (i_event.min () > _result.min ()) {
        _result.setMin (calc_min ());
      }
      if (i_event.max () < _result.max ()) {
        _result.setMax (calc_max ());
      }
    }
    public Object master () {
      return IntExpCardIntExp.this;
    }
    public int subscriberMask() {
      return MIN | MAX | VALUE | REMOVE;
    }
  }
}