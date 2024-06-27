class Amino {
    public static final Ala = new Amino("Alanine", "Ala", "")
    public static final Arg = new Amino("Arginine", "Arg", "")
    public static final Asn = new Amino("Aspargine", "Asn", "")
    public static final Asp = new Amino("Aspartatic acid", "Asp", "")
    public static final Cys = new Amino("Cystein", "Cys", "")
    public static final Glu = new Amino("Glutamic acid", "Glu", "")
    public static final Gln = new Amino("Glutamine", "Gln", "")
    public static final Gly = new Amino("Glycine", "Gly", "")
    public static final His = new Amino("Histidine", "His", "")
    public static final Lys = new Amino("Lysine", "Lys", "")
    public static final Phy = new Amino("Phenylalanine", "Phy", "")
    public static final Pro = new Amino("Proline", "Pro", "")
    public static final Ser = new Amino("Serine", "Ser", "")
    public static final Thr = new Amino("Threonine", "Thr", "")
    public static final Trp = new Amino("Tryptophan", "Trp", "")
    public static final Tyr = new Amino("Tyrosine", "Tyr", "")
    public static final Val = new Amino("Valine", "Val", "")

    String name
    String abbrev
    String letter

    private Amino(String name, String abbrev, String letter) {
        this.name   = name
        this.abbrev = abbrev
        this.letter = letter
    }
}

