class Protein {
    static List list = []
    String name
    List mutations = []

    Protein(String name) {
        list << this
        this.name = name
    }

    void mutatingAmino(Integer pos, Amino wild, Amino mutation) {
        //For now only a single mutation is allowed
        mutations << [pos, wild, mutation]
    }

    Amino getAmino(int pos, boolean wild) {
        Amino amino
        mutations.each {
            if (it[0] == pos) {
                if (wild) amino = it[1]; else amino = it[2]
            }
        }
        return amino
    }

    List mutation(Integer pos) {
        //Returns a placeholder ID to be able to find the definition
        return [this, pos]
    }
}
