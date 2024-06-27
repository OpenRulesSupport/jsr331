Ronald.init(this)

cq = compound(name: "Chloroquine", abbreviation: "cq", halfLife: 45.d)

CQ = drug(name: "Chloroquine", abbreviation: "CQ")
CQ.includes cpd: cq, qty: 300.mg, bioavail: 1.2
//Cmax of 1800 ng/mL (see http://www.ajtmh.org/cgi/content/abstract/74/3/407)
//final Cmax is dose * bioavail * (60/weight)


regimen = regimen()
regimen.take drug: CQ, qty: 2, at: 0.h
regimen.take drug: CQ, qty: 1, at: 6.h
regimen.take drug: CQ, qty: 1, at: 1.d
regimen.take drug: CQ, qty: 1, at: 2.d

CRT = protein("CRT")
CRT.mutatingAmino 76, Lys, Thr 

cqEffect = effect(
    name:       "General",
    formula:    {3.8 / (1 + km1/cq) },
    parameters: [km1: 68.0] //Hoshen98 microg/l
)

cqResistance = resistance(
    effect:     cqEffect,
    mutations:  [CRT.mutation(76)],
    parameters: [km1: 204.0] //Hoshen98
    //From Sidhu02 we would go twice this value
    //Sidhu is probably more reliable here
)

//Chart.test(cqEffect, cq, 1800)
sti = new STICompiler('/home/tra/malariaModel/trunk', 120, 24)
sti.generateFortran([CQ], [cqEffect])
