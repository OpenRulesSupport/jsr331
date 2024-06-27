class Drug {
    static list = []
    String name
    def cpdList = []

    Drug(String name) {
        this.name = name
        list << this
    }

    void includes(Map args) {
        //Compound cpd, Mass qtd, BigDecimal bioavail
        cpdList << [args['cpd'], args['qty'], args['bioavail']]
    }
}
