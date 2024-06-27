class Ronald {
    static init(env) {
        env.compound = {Map args ->
            Compound c = new Compound(args['name'])
            c.abbreviation = args['abbreviation'] ?: null
            c.halfLife = args['halfLife'] ?: null
            return c
        }

        env.drug = {Map args ->
            String name = args['name']
            String abbreviation = args['abbreviation']
            return new Drug(name)
        }

        env.regimen = { ->
            return new Regimen()
        }
        
        env.protein = {String name ->
            return new Protein(name)
        }

        env.effect = {Map args ->
            String name     = args['name']
            Closure formula = args['formula']
            Map parameters  = args['parameters']
            return new Effect(name, formula, parameters)
        }

        env.resistance = {Map args ->
            Effect effect   = args['effect']
            List mutations  = args['mutations']
            Map parameters  = args['parameters']
            Resistance res = new Resistance(mutations, parameters)
            effect.resistances << res
            return res
        }

        
        Time.plugNumbers()
        Mass.plugNumbers()
        
        env.Asn = Amino.Asn
        env.Arg = Amino.Arg
        env.Asn = Amino.Asn
        env.Asp = Amino.Asp
        env.Cys = Amino.Cys
        env.Glu = Amino.Glu
        env.Gln = Amino.Gln
        env.Gly = Amino.Gly
        env.His = Amino.His
        env.Lys = Amino.Lys
        env.Phy = Amino.Phy
        env.Pro = Amino.Pro
        env.Ser = Amino.Ser
        env.Thr = Amino.Thr
        env.Trp = Amino.Trp
        env.Tyr = Amino.Tyr
        env.Val = Amino.Val

    }
}
