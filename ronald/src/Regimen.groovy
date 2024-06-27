import Drug
import Time

class Regimen {
    static list = []
    def regimen = []

    Regimen() {
        list << this
    }

    void take(Map args) {
        //Drug drug, Number qty, Time at
        regimen << [args['drug'], args['qty'], args['at']]
    }
}
