/*
  This code only works if the source for the (non-free) STI simulator
  is available
*/

import org.codehaus.groovy.ast.expr.*

class STICompiler {
    String  srcDir
    Integer simTime
    Integer WHTime
    List    drugs
    List    effects
    List    cpdList //Cpd + mg (irrelevant) +  bioavail

    STICompiler(String srcDir, Integer simTime, Integer WHTime) {
        this.srcDir  = srcDir
        this.simTime = simTime
        this.WHTime  = WHTime
    }

    void patchFortranFile(String name, Map hookPatch) {
        //A patch might be used more than once in a file (typically a use mod_)
        String sep = System.getProperty("file.separator")
        String line
        StringBuffer bigBuffer = new StringBuffer("")
        String finalText

        FileWriter fw = new FileWriter(srcDir + sep + "fortran" + sep + name + ".f")
        BufferedReader br = new BufferedReader(
            new FileReader(srcDir + sep + "fortran" + sep + name + ".fortran"))
        //bigBuffer << "!This source file was automatically patched by Ronald\n"
        while ( (line = br.readLine()) != null) {
            bigBuffer << line + "\n"
        }
        finalText = bigBuffer.toString()
        hookPatch.each { k, v ->
            finalText = finalText.replace("!###" + k, v)
        }
        fw.write(finalText)
        br.close()
        fw.close()
    }

    void patchInit() {
        Map hookPatch = [:]
        hookPatch["INITCALL"] = "\tcall initDrugParameters(" + simTime + ", " + WHTime + ")\n"
        patchFortranFile("Init", hookPatch)
    }

    void patchDrugAction() {
        Map hookPatch = [:]
        String addText = ""

        cpdList.each {
            Compound cpd = it[0]
            BigDecimal bioavail = it[2]
            addText += "\tif(drg%dData%name.eq.\"" + cpd.abbreviation + "\") then\n"
            addText += "\t\tconvertDose = drg%doseList(pos)%dose * " + bioavail + " * (60.0/weight)\n"
            addText += "\tendif\n"
        }
        hookPatch["ABSORPTION"] = addText

        addText = ""
        cpdList.each {
            Compound cpd = it[0]
            addText += "\t\tif(drg%dData%name.eq.\"" + cpd.abbreviation + "\") then\n"
            addText += "\t\t\tcall computeHourConc(drg, " + cpd.halfLife.convertUnit(Time.HOUR).value.toInteger() + ")\n"
            addText += "\t\tendif\n"
        }
        hookPatch["HOURCONC"] = addText

        def drillExpression
        drillExpression = { expr, params ->
            switch (expr.class) {
              case BinaryExpression:
                return "(" + drillExpression(expr.leftExpression, params) + ")" +
                       expr.operation.text +
                       "(" + drillExpression(expr.rightExpression, params) + ")"
                break
              case ConstantExpression:
                return expr.text
                break
              case VariableExpression:
                if (params.containsKey(expr.text)) {
                    return params[expr.text].toString()
                }
                else {
                    return expr.text
                }
                break
              default: return ""
            }
        }

        addText = ""
        this.cpdList.each {
            addText += "\treal :: " + it[0].abbreviation + "\n"
        }
        hookPatch["PKVARDECL"] = addText

        addText = ""
        this.cpdList.each {
            addText += '\t\tif (drg%dData%name.eq."' + it[0].abbreviation + '") then\n'
            addText += "\t\t\t" + it[0].abbreviation + " = drg%hourConc(tPoint)\n"
            addText += "\t\tendif\n"
        }
        hookPatch["PKVARFILL"] = addText

        //Full execution required... general solution should be lazy
        def eachBetween = {list, c1, c2 ->
            boolean first = true
            list.each {
                if (!first) c2(); else first = false
                c1(it)
            }
        }

        addText = ""
        this.effects.each {
            def expr = it.code.getStatements()[0].getExpression()
            //This might require Fortran breaks
            addText += "\t!" + it.desc + "\n"
            boolean firstRes = true
            it.resistances.each { res ->
                if (firstRes) {
                    addText += "\tif ("
                    firstRes = false
                }
                else {
                    addText += "\telseif ("
                }
                eachBetween(res.mutations, { mut ->
                    print mut
                    addText += 'hasAmino(inf%iData%proteome, "' + mut[0].name + '", ' + mut[1] + ', "' + mut[0].getAmino(mut[1], false).letter + '")'
                }, {
                      addText += ".AND."
                })
                addText += ") then\n"
                addText += "\t\ttempFactor = " + drillExpression(expr, res.parameters) +" \n"
            }
            if (firstRes) {
                addText += "\ttempFactor = " + drillExpression(expr, it.parameters) +" \n"
            }
            else {
                addText += "\telse\n"
                addText += "\t\ttempFactor = " + drillExpression(expr, it.parameters) +" \n"
                addText += "\tendif\n"
            }
        }
        addText += "\tif (tempFactor.LT.calculateDrugFactor) then\n"
        addText += "\t\tcalculateDrugFactor = tempFactor\n"
        addText += "\tendif\n"
        hookPatch["PKCALL"] = addText

        println "XXX Convert drug factor to hourly part"

        patchFortranFile("DrugAction", hookPatch)
    }

    void generateFortran(List drugs, List effects) {
        this.drugs   = drugs
        this.effects = effects

        this.cpdList = []
        drugs.each {
            it.cpdList.each {this.cpdList << it}
        }
        patchInit()
        patchDrugAction()
    }
}
