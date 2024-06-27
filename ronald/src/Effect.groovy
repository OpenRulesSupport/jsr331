import org.codehaus.groovy.ast.ASTNode

class Effect {
    String desc
    Closure formula
    Map parameters
    List resistances = []
    ASTNode code

    Effect(String desc, Closure formula, Map parameters) {
        this.desc = desc
        this.formula = formula
        this.parameters = parameters
        this.resistances = []
        this.code = formula.getMetaClass().getClassNode().getMethods("doCall")[0].code
    }
}
