class Time implements Comparable {
    // constants
    public static BigDecimal SECOND = 1
    public static BigDecimal MINUTE = 60 * SECOND
    public static BigDecimal HOUR = 60 * MINUTE
    public static BigDecimal DAY = 24 * HOUR

    BigDecimal value
    BigDecimal unit
    
    Time(BigDecimal value, BigDecimal unit) {
        this.value = value
        this.unit = unit
    }
    
    Time convertUnit(BigDecimal unit) {
        return new Time((value * this.unit)/unit, unit)
    }
    
    int compareTo(other) {
        if(this.unit == other.unit)
            return this.value <=> other.value
        return this.value <=> other.convertUnit(this.unit)
    }

    static void plug(MetaClassImpl meta) {
        meta.getS << { -> new Time(delegate, Time.SECOND) }
        meta.getM << { -> new Time(delegate, Time.MINUTE) }
        meta.getH << { -> new Time(delegate, Time.HOUR) }
        meta.getD << { -> new Time(delegate, Time.DAY) }
    }
    
    static void plugNumbers() {
        plug(BigDecimal.metaClass)
        plug(Integer.metaClass)
    }
    
    String toString() {
        String prefix;
        switch (unit) {
            case SECOND: prefix = "s"
            case MINUTE: prefix = "m"
            case HOUR  : prefix = "h"
            case DAY   : prefix = "d"
        }
        "${value}${prefix}"
    }
}


