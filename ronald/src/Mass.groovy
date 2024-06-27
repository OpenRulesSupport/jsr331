class Mass implements Comparable {
    public static BigDecimal PG = 1
    public static BigDecimal NG = 1000*PG
    public static BigDecimal MICROG = 1000*NG
    public static BigDecimal MG = 1000*MICROG
    public static BigDecimal G = 1000*MG
    BigDecimal value
    BigDecimal unit
    
    Mass(BigDecimal value, BigDecimal unit) {
        this.value = value
        this.unit = unit
    }

    Mass convertUnit(BigDecimal unit) {
        return new Mass((value * this.unit)/unit, unit)
    }
    
    int compareTo(other) {
        if(this.unit == other.unit)
            return this.value <=> other.value
        return this.value <=> other.convertUnit(this.unit)
    }
    
    static void plug(MetaClassImpl meta) {
        meta.getPg     << {-> new Mass(delegate, Mass.PG)}
        meta.getNg     << {-> new Mass(delegate, Mass.NG)}
        meta.getMicrog << {-> new Mass(delegate, Mass.MICROG)}
        meta.getMg     << {-> new Mass(delegate, Mass.MG)}
        meta.getG      << {-> new Mass(delegate, Mass.G)}
    }
    
    static void plugNumbers() {
        plug(BigDecimal.metaClass)
        plug(Integer.metaClass)
    }

    String toString() {
        "$value $unit"
    }
}


