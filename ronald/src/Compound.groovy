import Time
class Compound {
    static compoundList = []

    String   name
    String   abbreviation
    Time     halfLife

    Compound(String name) {
        this.name = name
        compoundList << this
    }

    String toString() {
        "${name} (${abbreviation}) - half life ${halfLife}"
    }
}
