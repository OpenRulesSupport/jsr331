package org.jcp.jsr331.samples;

public class CoinCombinationsChatGPT {
    
    static int n = 0;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int[] coinDenominations = { 1, 2, 5, 10, 20, 50, 100 };
        int targetAmount = 100;
        int[] currentCombination = new int[coinDenominations.length];

        findCoinCombinations(coinDenominations, targetAmount, 0, currentCombination);
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + (endTime - startTime + 1) + " ms");
    }

    public static void findCoinCombinations(int[] coinDenominations, int targetAmount, int index,
            int[] currentCombination) {
        if (targetAmount == 0) {
            printCombination(coinDenominations, currentCombination);
            return;
        }

        if (targetAmount < 0 || index == coinDenominations.length) {
            return;
        }

        // Include the current coin denomination
        currentCombination[index]++;
        findCoinCombinations(coinDenominations, targetAmount - coinDenominations[index], index, currentCombination);

        // Exclude the current coin denomination and move to the next
        currentCombination[index]--;
        findCoinCombinations(coinDenominations, targetAmount, index + 1, currentCombination);
    }

    public static void printCombination(int[] coinDenominations, int[] currentCombination) {
        n++;
        System.out.print("Combination " + n + ": "); // added by JF
        for (int i = 0; i < coinDenominations.length; i++) {
            if (currentCombination[i] > 0) {
                System.out.print(currentCombination[i] + " x " + coinDenominations[i] + "c ");
            }
        }
        System.out.println();
    }
}

/*
Combination 1: 100 x 1c 
Combination 2: 98 x 1c 1 x 2c 
Combination 3: 96 x 1c 2 x 2c 
Combination 4: 95 x 1c 1 x 5c 
Combination 5: 94 x 1c 3 x 2c 
Combination 6: 93 x 1c 1 x 2c 1 x 5c 
Combination 7: 92 x 1c 4 x 2c 
Combination 8: 91 x 1c 2 x 2c 1 x 5c 
Combination 9: 90 x 1c 5 x 2c 
Combination 10: 90 x 1c 2 x 5c 
Combination 11: 90 x 1c 1 x 10c 
Combination 12: 89 x 1c 3 x 2c 1 x 5c 
Combination 13: 88 x 1c 6 x 2c 
Combination 14: 88 x 1c 1 x 2c 2 x 5c 
Combination 15: 88 x 1c 1 x 2c 1 x 10c 
Combination 16: 87 x 1c 4 x 2c 1 x 5c 
Combination 17: 86 x 1c 7 x 2c 
Combination 18: 86 x 1c 2 x 2c 2 x 5c 
Combination 19: 86 x 1c 2 x 2c 1 x 10c 
Combination 20: 85 x 1c 5 x 2c 1 x 5c 
Combination 21: 85 x 1c 3 x 5c 
Combination 22: 85 x 1c 1 x 5c 1 x 10c 
Combination 23: 84 x 1c 8 x 2c 
Combination 24: 84 x 1c 3 x 2c 2 x 5c 
Combination 25: 84 x 1c 3 x 2c 1 x 10c 
Combination 26: 83 x 1c 6 x 2c 1 x 5c 
Combination 27: 83 x 1c 1 x 2c 3 x 5c 
Combination 28: 83 x 1c 1 x 2c 1 x 5c 1 x 10c 
Combination 29: 82 x 1c 9 x 2c 
Combination 30: 82 x 1c 4 x 2c 2 x 5c 
Combination 31: 82 x 1c 4 x 2c 1 x 10c 
Combination 32: 81 x 1c 7 x 2c 1 x 5c 
Combination 33: 81 x 1c 2 x 2c 3 x 5c 
Combination 34: 81 x 1c 2 x 2c 1 x 5c 1 x 10c 
Combination 35: 80 x 1c 10 x 2c 
Combination 36: 80 x 1c 5 x 2c 2 x 5c 
Combination 37: 80 x 1c 5 x 2c 1 x 10c 
Combination 38: 80 x 1c 4 x 5c 
Combination 39: 80 x 1c 2 x 5c 1 x 10c 
Combination 40: 80 x 1c 2 x 10c 
Combination 41: 80 x 1c 1 x 20c 
Combination 42: 79 x 1c 8 x 2c 1 x 5c 
Combination 43: 79 x 1c 3 x 2c 3 x 5c 
Combination 44: 79 x 1c 3 x 2c 1 x 5c 1 x 10c 
Combination 45: 78 x 1c 11 x 2c 
Combination 46: 78 x 1c 6 x 2c 2 x 5c 
Combination 47: 78 x 1c 6 x 2c 1 x 10c 
Combination 48: 78 x 1c 1 x 2c 4 x 5c 
Combination 49: 78 x 1c 1 x 2c 2 x 5c 1 x 10c 
Combination 50: 78 x 1c 1 x 2c 2 x 10c 
Combination 51: 78 x 1c 1 x 2c 1 x 20c 
Combination 52: 77 x 1c 9 x 2c 1 x 5c 
Combination 53: 77 x 1c 4 x 2c 3 x 5c 
Combination 54: 77 x 1c 4 x 2c 1 x 5c 1 x 10c 
Combination 55: 76 x 1c 12 x 2c 
Combination 56: 76 x 1c 7 x 2c 2 x 5c 
Combination 57: 76 x 1c 7 x 2c 1 x 10c 
Combination 58: 76 x 1c 2 x 2c 4 x 5c 
Combination 59: 76 x 1c 2 x 2c 2 x 5c 1 x 10c 
Combination 60: 76 x 1c 2 x 2c 2 x 10c 
Combination 61: 76 x 1c 2 x 2c 1 x 20c 
Combination 62: 75 x 1c 10 x 2c 1 x 5c 
Combination 63: 75 x 1c 5 x 2c 3 x 5c 
Combination 64: 75 x 1c 5 x 2c 1 x 5c 1 x 10c 
Combination 65: 75 x 1c 5 x 5c 
Combination 66: 75 x 1c 3 x 5c 1 x 10c 
Combination 67: 75 x 1c 1 x 5c 2 x 10c 
Combination 68: 75 x 1c 1 x 5c 1 x 20c 
Combination 69: 74 x 1c 13 x 2c 
Combination 70: 74 x 1c 8 x 2c 2 x 5c 
Combination 71: 74 x 1c 8 x 2c 1 x 10c 
Combination 72: 74 x 1c 3 x 2c 4 x 5c 
Combination 73: 74 x 1c 3 x 2c 2 x 5c 1 x 10c 
Combination 74: 74 x 1c 3 x 2c 2 x 10c 
Combination 75: 74 x 1c 3 x 2c 1 x 20c 
Combination 76: 73 x 1c 11 x 2c 1 x 5c 
Combination 77: 73 x 1c 6 x 2c 3 x 5c 
Combination 78: 73 x 1c 6 x 2c 1 x 5c 1 x 10c 
Combination 79: 73 x 1c 1 x 2c 5 x 5c 
Combination 80: 73 x 1c 1 x 2c 3 x 5c 1 x 10c 
Combination 81: 73 x 1c 1 x 2c 1 x 5c 2 x 10c 
Combination 82: 73 x 1c 1 x 2c 1 x 5c 1 x 20c 
Combination 83: 72 x 1c 14 x 2c 
Combination 84: 72 x 1c 9 x 2c 2 x 5c 
Combination 85: 72 x 1c 9 x 2c 1 x 10c 
Combination 86: 72 x 1c 4 x 2c 4 x 5c 
Combination 87: 72 x 1c 4 x 2c 2 x 5c 1 x 10c 
Combination 88: 72 x 1c 4 x 2c 2 x 10c 
Combination 89: 72 x 1c 4 x 2c 1 x 20c 
Combination 90: 71 x 1c 12 x 2c 1 x 5c 
Combination 91: 71 x 1c 7 x 2c 3 x 5c 
Combination 92: 71 x 1c 7 x 2c 1 x 5c 1 x 10c 
Combination 93: 71 x 1c 2 x 2c 5 x 5c 
Combination 94: 71 x 1c 2 x 2c 3 x 5c 1 x 10c 
Combination 95: 71 x 1c 2 x 2c 1 x 5c 2 x 10c 
Combination 96: 71 x 1c 2 x 2c 1 x 5c 1 x 20c 
Combination 97: 70 x 1c 15 x 2c 
Combination 98: 70 x 1c 10 x 2c 2 x 5c 
Combination 99: 70 x 1c 10 x 2c 1 x 10c 
Combination 100: 70 x 1c 5 x 2c 4 x 5c 
Combination 101: 70 x 1c 5 x 2c 2 x 5c 1 x 10c 
Combination 102: 70 x 1c 5 x 2c 2 x 10c 
Combination 103: 70 x 1c 5 x 2c 1 x 20c 
Combination 104: 70 x 1c 6 x 5c 
Combination 105: 70 x 1c 4 x 5c 1 x 10c 
Combination 106: 70 x 1c 2 x 5c 2 x 10c 
Combination 107: 70 x 1c 2 x 5c 1 x 20c 
Combination 108: 70 x 1c 3 x 10c 
Combination 109: 70 x 1c 1 x 10c 1 x 20c 
Combination 110: 69 x 1c 13 x 2c 1 x 5c 
Combination 111: 69 x 1c 8 x 2c 3 x 5c 
Combination 112: 69 x 1c 8 x 2c 1 x 5c 1 x 10c 
Combination 113: 69 x 1c 3 x 2c 5 x 5c 
Combination 114: 69 x 1c 3 x 2c 3 x 5c 1 x 10c 
Combination 115: 69 x 1c 3 x 2c 1 x 5c 2 x 10c 
Combination 116: 69 x 1c 3 x 2c 1 x 5c 1 x 20c 
Combination 117: 68 x 1c 16 x 2c 
Combination 118: 68 x 1c 11 x 2c 2 x 5c 
Combination 119: 68 x 1c 11 x 2c 1 x 10c 
Combination 120: 68 x 1c 6 x 2c 4 x 5c 
Combination 121: 68 x 1c 6 x 2c 2 x 5c 1 x 10c 
Combination 122: 68 x 1c 6 x 2c 2 x 10c 
Combination 123: 68 x 1c 6 x 2c 1 x 20c 
Combination 124: 68 x 1c 1 x 2c 6 x 5c 
Combination 125: 68 x 1c 1 x 2c 4 x 5c 1 x 10c 
Combination 126: 68 x 1c 1 x 2c 2 x 5c 2 x 10c 
Combination 127: 68 x 1c 1 x 2c 2 x 5c 1 x 20c 
Combination 128: 68 x 1c 1 x 2c 3 x 10c 
Combination 129: 68 x 1c 1 x 2c 1 x 10c 1 x 20c 
Combination 130: 67 x 1c 14 x 2c 1 x 5c 
Combination 131: 67 x 1c 9 x 2c 3 x 5c 
Combination 132: 67 x 1c 9 x 2c 1 x 5c 1 x 10c 
Combination 133: 67 x 1c 4 x 2c 5 x 5c 
Combination 134: 67 x 1c 4 x 2c 3 x 5c 1 x 10c 
Combination 135: 67 x 1c 4 x 2c 1 x 5c 2 x 10c 
Combination 136: 67 x 1c 4 x 2c 1 x 5c 1 x 20c 
Combination 137: 66 x 1c 17 x 2c 
Combination 138: 66 x 1c 12 x 2c 2 x 5c 
Combination 139: 66 x 1c 12 x 2c 1 x 10c 
Combination 140: 66 x 1c 7 x 2c 4 x 5c 
Combination 141: 66 x 1c 7 x 2c 2 x 5c 1 x 10c 
Combination 142: 66 x 1c 7 x 2c 2 x 10c 
Combination 143: 66 x 1c 7 x 2c 1 x 20c 
Combination 144: 66 x 1c 2 x 2c 6 x 5c 
Combination 145: 66 x 1c 2 x 2c 4 x 5c 1 x 10c 
Combination 146: 66 x 1c 2 x 2c 2 x 5c 2 x 10c 
Combination 147: 66 x 1c 2 x 2c 2 x 5c 1 x 20c 
Combination 148: 66 x 1c 2 x 2c 3 x 10c 
Combination 149: 66 x 1c 2 x 2c 1 x 10c 1 x 20c 
Combination 150: 65 x 1c 15 x 2c 1 x 5c 
Combination 151: 65 x 1c 10 x 2c 3 x 5c 
Combination 152: 65 x 1c 10 x 2c 1 x 5c 1 x 10c 
Combination 153: 65 x 1c 5 x 2c 5 x 5c 
Combination 154: 65 x 1c 5 x 2c 3 x 5c 1 x 10c 
Combination 155: 65 x 1c 5 x 2c 1 x 5c 2 x 10c 
Combination 156: 65 x 1c 5 x 2c 1 x 5c 1 x 20c 
Combination 157: 65 x 1c 7 x 5c 
Combination 158: 65 x 1c 5 x 5c 1 x 10c 
Combination 159: 65 x 1c 3 x 5c 2 x 10c 
Combination 160: 65 x 1c 3 x 5c 1 x 20c 
Combination 161: 65 x 1c 1 x 5c 3 x 10c 
Combination 162: 65 x 1c 1 x 5c 1 x 10c 1 x 20c 
Combination 163: 64 x 1c 18 x 2c 
Combination 164: 64 x 1c 13 x 2c 2 x 5c 
Combination 165: 64 x 1c 13 x 2c 1 x 10c 
Combination 166: 64 x 1c 8 x 2c 4 x 5c 
Combination 167: 64 x 1c 8 x 2c 2 x 5c 1 x 10c 
Combination 168: 64 x 1c 8 x 2c 2 x 10c 
Combination 169: 64 x 1c 8 x 2c 1 x 20c 
Combination 170: 64 x 1c 3 x 2c 6 x 5c 
Combination 171: 64 x 1c 3 x 2c 4 x 5c 1 x 10c 
Combination 172: 64 x 1c 3 x 2c 2 x 5c 2 x 10c 
Combination 173: 64 x 1c 3 x 2c 2 x 5c 1 x 20c 
Combination 174: 64 x 1c 3 x 2c 3 x 10c 
Combination 175: 64 x 1c 3 x 2c 1 x 10c 1 x 20c 
Combination 176: 63 x 1c 16 x 2c 1 x 5c 
Combination 177: 63 x 1c 11 x 2c 3 x 5c 
Combination 178: 63 x 1c 11 x 2c 1 x 5c 1 x 10c 
Combination 179: 63 x 1c 6 x 2c 5 x 5c 
Combination 180: 63 x 1c 6 x 2c 3 x 5c 1 x 10c 
Combination 181: 63 x 1c 6 x 2c 1 x 5c 2 x 10c 
Combination 182: 63 x 1c 6 x 2c 1 x 5c 1 x 20c 
Combination 183: 63 x 1c 1 x 2c 7 x 5c 
Combination 184: 63 x 1c 1 x 2c 5 x 5c 1 x 10c 
Combination 185: 63 x 1c 1 x 2c 3 x 5c 2 x 10c 
Combination 186: 63 x 1c 1 x 2c 3 x 5c 1 x 20c 
Combination 187: 63 x 1c 1 x 2c 1 x 5c 3 x 10c 
Combination 188: 63 x 1c 1 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 189: 62 x 1c 19 x 2c 
Combination 190: 62 x 1c 14 x 2c 2 x 5c 
Combination 191: 62 x 1c 14 x 2c 1 x 10c 
Combination 192: 62 x 1c 9 x 2c 4 x 5c 
Combination 193: 62 x 1c 9 x 2c 2 x 5c 1 x 10c 
Combination 194: 62 x 1c 9 x 2c 2 x 10c 
Combination 195: 62 x 1c 9 x 2c 1 x 20c 
Combination 196: 62 x 1c 4 x 2c 6 x 5c 
Combination 197: 62 x 1c 4 x 2c 4 x 5c 1 x 10c 
Combination 198: 62 x 1c 4 x 2c 2 x 5c 2 x 10c 
Combination 199: 62 x 1c 4 x 2c 2 x 5c 1 x 20c 
Combination 200: 62 x 1c 4 x 2c 3 x 10c 
Combination 201: 62 x 1c 4 x 2c 1 x 10c 1 x 20c 
Combination 202: 61 x 1c 17 x 2c 1 x 5c 
Combination 203: 61 x 1c 12 x 2c 3 x 5c 
Combination 204: 61 x 1c 12 x 2c 1 x 5c 1 x 10c 
Combination 205: 61 x 1c 7 x 2c 5 x 5c 
Combination 206: 61 x 1c 7 x 2c 3 x 5c 1 x 10c 
Combination 207: 61 x 1c 7 x 2c 1 x 5c 2 x 10c 
Combination 208: 61 x 1c 7 x 2c 1 x 5c 1 x 20c 
Combination 209: 61 x 1c 2 x 2c 7 x 5c 
Combination 210: 61 x 1c 2 x 2c 5 x 5c 1 x 10c 
Combination 211: 61 x 1c 2 x 2c 3 x 5c 2 x 10c 
Combination 212: 61 x 1c 2 x 2c 3 x 5c 1 x 20c 
Combination 213: 61 x 1c 2 x 2c 1 x 5c 3 x 10c 
Combination 214: 61 x 1c 2 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 215: 60 x 1c 20 x 2c 
Combination 216: 60 x 1c 15 x 2c 2 x 5c 
Combination 217: 60 x 1c 15 x 2c 1 x 10c 
Combination 218: 60 x 1c 10 x 2c 4 x 5c 
Combination 219: 60 x 1c 10 x 2c 2 x 5c 1 x 10c 
Combination 220: 60 x 1c 10 x 2c 2 x 10c 
Combination 221: 60 x 1c 10 x 2c 1 x 20c 
Combination 222: 60 x 1c 5 x 2c 6 x 5c 
Combination 223: 60 x 1c 5 x 2c 4 x 5c 1 x 10c 
Combination 224: 60 x 1c 5 x 2c 2 x 5c 2 x 10c 
Combination 225: 60 x 1c 5 x 2c 2 x 5c 1 x 20c 
Combination 226: 60 x 1c 5 x 2c 3 x 10c 
Combination 227: 60 x 1c 5 x 2c 1 x 10c 1 x 20c 
Combination 228: 60 x 1c 8 x 5c 
Combination 229: 60 x 1c 6 x 5c 1 x 10c 
Combination 230: 60 x 1c 4 x 5c 2 x 10c 
Combination 231: 60 x 1c 4 x 5c 1 x 20c 
Combination 232: 60 x 1c 2 x 5c 3 x 10c 
Combination 233: 60 x 1c 2 x 5c 1 x 10c 1 x 20c 
Combination 234: 60 x 1c 4 x 10c 
Combination 235: 60 x 1c 2 x 10c 1 x 20c 
Combination 236: 60 x 1c 2 x 20c 
Combination 237: 59 x 1c 18 x 2c 1 x 5c 
Combination 238: 59 x 1c 13 x 2c 3 x 5c 
Combination 239: 59 x 1c 13 x 2c 1 x 5c 1 x 10c 
Combination 240: 59 x 1c 8 x 2c 5 x 5c 
Combination 241: 59 x 1c 8 x 2c 3 x 5c 1 x 10c 
Combination 242: 59 x 1c 8 x 2c 1 x 5c 2 x 10c 
Combination 243: 59 x 1c 8 x 2c 1 x 5c 1 x 20c 
Combination 244: 59 x 1c 3 x 2c 7 x 5c 
Combination 245: 59 x 1c 3 x 2c 5 x 5c 1 x 10c 
Combination 246: 59 x 1c 3 x 2c 3 x 5c 2 x 10c 
Combination 247: 59 x 1c 3 x 2c 3 x 5c 1 x 20c 
Combination 248: 59 x 1c 3 x 2c 1 x 5c 3 x 10c 
Combination 249: 59 x 1c 3 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 250: 58 x 1c 21 x 2c 
Combination 251: 58 x 1c 16 x 2c 2 x 5c 
Combination 252: 58 x 1c 16 x 2c 1 x 10c 
Combination 253: 58 x 1c 11 x 2c 4 x 5c 
Combination 254: 58 x 1c 11 x 2c 2 x 5c 1 x 10c 
Combination 255: 58 x 1c 11 x 2c 2 x 10c 
Combination 256: 58 x 1c 11 x 2c 1 x 20c 
Combination 257: 58 x 1c 6 x 2c 6 x 5c 
Combination 258: 58 x 1c 6 x 2c 4 x 5c 1 x 10c 
Combination 259: 58 x 1c 6 x 2c 2 x 5c 2 x 10c 
Combination 260: 58 x 1c 6 x 2c 2 x 5c 1 x 20c 
Combination 261: 58 x 1c 6 x 2c 3 x 10c 
Combination 262: 58 x 1c 6 x 2c 1 x 10c 1 x 20c 
Combination 263: 58 x 1c 1 x 2c 8 x 5c 
Combination 264: 58 x 1c 1 x 2c 6 x 5c 1 x 10c 
Combination 265: 58 x 1c 1 x 2c 4 x 5c 2 x 10c 
Combination 266: 58 x 1c 1 x 2c 4 x 5c 1 x 20c 
Combination 267: 58 x 1c 1 x 2c 2 x 5c 3 x 10c 
Combination 268: 58 x 1c 1 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 269: 58 x 1c 1 x 2c 4 x 10c 
Combination 270: 58 x 1c 1 x 2c 2 x 10c 1 x 20c 
Combination 271: 58 x 1c 1 x 2c 2 x 20c 
Combination 272: 57 x 1c 19 x 2c 1 x 5c 
Combination 273: 57 x 1c 14 x 2c 3 x 5c 
Combination 274: 57 x 1c 14 x 2c 1 x 5c 1 x 10c 
Combination 275: 57 x 1c 9 x 2c 5 x 5c 
Combination 276: 57 x 1c 9 x 2c 3 x 5c 1 x 10c 
Combination 277: 57 x 1c 9 x 2c 1 x 5c 2 x 10c 
Combination 278: 57 x 1c 9 x 2c 1 x 5c 1 x 20c 
Combination 279: 57 x 1c 4 x 2c 7 x 5c 
Combination 280: 57 x 1c 4 x 2c 5 x 5c 1 x 10c 
Combination 281: 57 x 1c 4 x 2c 3 x 5c 2 x 10c 
Combination 282: 57 x 1c 4 x 2c 3 x 5c 1 x 20c 
Combination 283: 57 x 1c 4 x 2c 1 x 5c 3 x 10c 
Combination 284: 57 x 1c 4 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 285: 56 x 1c 22 x 2c 
Combination 286: 56 x 1c 17 x 2c 2 x 5c 
Combination 287: 56 x 1c 17 x 2c 1 x 10c 
Combination 288: 56 x 1c 12 x 2c 4 x 5c 
Combination 289: 56 x 1c 12 x 2c 2 x 5c 1 x 10c 
Combination 290: 56 x 1c 12 x 2c 2 x 10c 
Combination 291: 56 x 1c 12 x 2c 1 x 20c 
Combination 292: 56 x 1c 7 x 2c 6 x 5c 
Combination 293: 56 x 1c 7 x 2c 4 x 5c 1 x 10c 
Combination 294: 56 x 1c 7 x 2c 2 x 5c 2 x 10c 
Combination 295: 56 x 1c 7 x 2c 2 x 5c 1 x 20c 
Combination 296: 56 x 1c 7 x 2c 3 x 10c 
Combination 297: 56 x 1c 7 x 2c 1 x 10c 1 x 20c 
Combination 298: 56 x 1c 2 x 2c 8 x 5c 
Combination 299: 56 x 1c 2 x 2c 6 x 5c 1 x 10c 
Combination 300: 56 x 1c 2 x 2c 4 x 5c 2 x 10c 
Combination 301: 56 x 1c 2 x 2c 4 x 5c 1 x 20c 
Combination 302: 56 x 1c 2 x 2c 2 x 5c 3 x 10c 
Combination 303: 56 x 1c 2 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 304: 56 x 1c 2 x 2c 4 x 10c 
Combination 305: 56 x 1c 2 x 2c 2 x 10c 1 x 20c 
Combination 306: 56 x 1c 2 x 2c 2 x 20c 
Combination 307: 55 x 1c 20 x 2c 1 x 5c 
Combination 308: 55 x 1c 15 x 2c 3 x 5c 
Combination 309: 55 x 1c 15 x 2c 1 x 5c 1 x 10c 
Combination 310: 55 x 1c 10 x 2c 5 x 5c 
Combination 311: 55 x 1c 10 x 2c 3 x 5c 1 x 10c 
Combination 312: 55 x 1c 10 x 2c 1 x 5c 2 x 10c 
Combination 313: 55 x 1c 10 x 2c 1 x 5c 1 x 20c 
Combination 314: 55 x 1c 5 x 2c 7 x 5c 
Combination 315: 55 x 1c 5 x 2c 5 x 5c 1 x 10c 
Combination 316: 55 x 1c 5 x 2c 3 x 5c 2 x 10c 
Combination 317: 55 x 1c 5 x 2c 3 x 5c 1 x 20c 
Combination 318: 55 x 1c 5 x 2c 1 x 5c 3 x 10c 
Combination 319: 55 x 1c 5 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 320: 55 x 1c 9 x 5c 
Combination 321: 55 x 1c 7 x 5c 1 x 10c 
Combination 322: 55 x 1c 5 x 5c 2 x 10c 
Combination 323: 55 x 1c 5 x 5c 1 x 20c 
Combination 324: 55 x 1c 3 x 5c 3 x 10c 
Combination 325: 55 x 1c 3 x 5c 1 x 10c 1 x 20c 
Combination 326: 55 x 1c 1 x 5c 4 x 10c 
Combination 327: 55 x 1c 1 x 5c 2 x 10c 1 x 20c 
Combination 328: 55 x 1c 1 x 5c 2 x 20c 
Combination 329: 54 x 1c 23 x 2c 
Combination 330: 54 x 1c 18 x 2c 2 x 5c 
Combination 331: 54 x 1c 18 x 2c 1 x 10c 
Combination 332: 54 x 1c 13 x 2c 4 x 5c 
Combination 333: 54 x 1c 13 x 2c 2 x 5c 1 x 10c 
Combination 334: 54 x 1c 13 x 2c 2 x 10c 
Combination 335: 54 x 1c 13 x 2c 1 x 20c 
Combination 336: 54 x 1c 8 x 2c 6 x 5c 
Combination 337: 54 x 1c 8 x 2c 4 x 5c 1 x 10c 
Combination 338: 54 x 1c 8 x 2c 2 x 5c 2 x 10c 
Combination 339: 54 x 1c 8 x 2c 2 x 5c 1 x 20c 
Combination 340: 54 x 1c 8 x 2c 3 x 10c 
Combination 341: 54 x 1c 8 x 2c 1 x 10c 1 x 20c 
Combination 342: 54 x 1c 3 x 2c 8 x 5c 
Combination 343: 54 x 1c 3 x 2c 6 x 5c 1 x 10c 
Combination 344: 54 x 1c 3 x 2c 4 x 5c 2 x 10c 
Combination 345: 54 x 1c 3 x 2c 4 x 5c 1 x 20c 
Combination 346: 54 x 1c 3 x 2c 2 x 5c 3 x 10c 
Combination 347: 54 x 1c 3 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 348: 54 x 1c 3 x 2c 4 x 10c 
Combination 349: 54 x 1c 3 x 2c 2 x 10c 1 x 20c 
Combination 350: 54 x 1c 3 x 2c 2 x 20c 
Combination 351: 53 x 1c 21 x 2c 1 x 5c 
Combination 352: 53 x 1c 16 x 2c 3 x 5c 
Combination 353: 53 x 1c 16 x 2c 1 x 5c 1 x 10c 
Combination 354: 53 x 1c 11 x 2c 5 x 5c 
Combination 355: 53 x 1c 11 x 2c 3 x 5c 1 x 10c 
Combination 356: 53 x 1c 11 x 2c 1 x 5c 2 x 10c 
Combination 357: 53 x 1c 11 x 2c 1 x 5c 1 x 20c 
Combination 358: 53 x 1c 6 x 2c 7 x 5c 
Combination 359: 53 x 1c 6 x 2c 5 x 5c 1 x 10c 
Combination 360: 53 x 1c 6 x 2c 3 x 5c 2 x 10c 
Combination 361: 53 x 1c 6 x 2c 3 x 5c 1 x 20c 
Combination 362: 53 x 1c 6 x 2c 1 x 5c 3 x 10c 
Combination 363: 53 x 1c 6 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 364: 53 x 1c 1 x 2c 9 x 5c 
Combination 365: 53 x 1c 1 x 2c 7 x 5c 1 x 10c 
Combination 366: 53 x 1c 1 x 2c 5 x 5c 2 x 10c 
Combination 367: 53 x 1c 1 x 2c 5 x 5c 1 x 20c 
Combination 368: 53 x 1c 1 x 2c 3 x 5c 3 x 10c 
Combination 369: 53 x 1c 1 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 370: 53 x 1c 1 x 2c 1 x 5c 4 x 10c 
Combination 371: 53 x 1c 1 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 372: 53 x 1c 1 x 2c 1 x 5c 2 x 20c 
Combination 373: 52 x 1c 24 x 2c 
Combination 374: 52 x 1c 19 x 2c 2 x 5c 
Combination 375: 52 x 1c 19 x 2c 1 x 10c 
Combination 376: 52 x 1c 14 x 2c 4 x 5c 
Combination 377: 52 x 1c 14 x 2c 2 x 5c 1 x 10c 
Combination 378: 52 x 1c 14 x 2c 2 x 10c 
Combination 379: 52 x 1c 14 x 2c 1 x 20c 
Combination 380: 52 x 1c 9 x 2c 6 x 5c 
Combination 381: 52 x 1c 9 x 2c 4 x 5c 1 x 10c 
Combination 382: 52 x 1c 9 x 2c 2 x 5c 2 x 10c 
Combination 383: 52 x 1c 9 x 2c 2 x 5c 1 x 20c 
Combination 384: 52 x 1c 9 x 2c 3 x 10c 
Combination 385: 52 x 1c 9 x 2c 1 x 10c 1 x 20c 
Combination 386: 52 x 1c 4 x 2c 8 x 5c 
Combination 387: 52 x 1c 4 x 2c 6 x 5c 1 x 10c 
Combination 388: 52 x 1c 4 x 2c 4 x 5c 2 x 10c 
Combination 389: 52 x 1c 4 x 2c 4 x 5c 1 x 20c 
Combination 390: 52 x 1c 4 x 2c 2 x 5c 3 x 10c 
Combination 391: 52 x 1c 4 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 392: 52 x 1c 4 x 2c 4 x 10c 
Combination 393: 52 x 1c 4 x 2c 2 x 10c 1 x 20c 
Combination 394: 52 x 1c 4 x 2c 2 x 20c 
Combination 395: 51 x 1c 22 x 2c 1 x 5c 
Combination 396: 51 x 1c 17 x 2c 3 x 5c 
Combination 397: 51 x 1c 17 x 2c 1 x 5c 1 x 10c 
Combination 398: 51 x 1c 12 x 2c 5 x 5c 
Combination 399: 51 x 1c 12 x 2c 3 x 5c 1 x 10c 
Combination 400: 51 x 1c 12 x 2c 1 x 5c 2 x 10c 
Combination 401: 51 x 1c 12 x 2c 1 x 5c 1 x 20c 
Combination 402: 51 x 1c 7 x 2c 7 x 5c 
Combination 403: 51 x 1c 7 x 2c 5 x 5c 1 x 10c 
Combination 404: 51 x 1c 7 x 2c 3 x 5c 2 x 10c 
Combination 405: 51 x 1c 7 x 2c 3 x 5c 1 x 20c 
Combination 406: 51 x 1c 7 x 2c 1 x 5c 3 x 10c 
Combination 407: 51 x 1c 7 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 408: 51 x 1c 2 x 2c 9 x 5c 
Combination 409: 51 x 1c 2 x 2c 7 x 5c 1 x 10c 
Combination 410: 51 x 1c 2 x 2c 5 x 5c 2 x 10c 
Combination 411: 51 x 1c 2 x 2c 5 x 5c 1 x 20c 
Combination 412: 51 x 1c 2 x 2c 3 x 5c 3 x 10c 
Combination 413: 51 x 1c 2 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 414: 51 x 1c 2 x 2c 1 x 5c 4 x 10c 
Combination 415: 51 x 1c 2 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 416: 51 x 1c 2 x 2c 1 x 5c 2 x 20c 
Combination 417: 50 x 1c 25 x 2c 
Combination 418: 50 x 1c 20 x 2c 2 x 5c 
Combination 419: 50 x 1c 20 x 2c 1 x 10c 
Combination 420: 50 x 1c 15 x 2c 4 x 5c 
Combination 421: 50 x 1c 15 x 2c 2 x 5c 1 x 10c 
Combination 422: 50 x 1c 15 x 2c 2 x 10c 
Combination 423: 50 x 1c 15 x 2c 1 x 20c 
Combination 424: 50 x 1c 10 x 2c 6 x 5c 
Combination 425: 50 x 1c 10 x 2c 4 x 5c 1 x 10c 
Combination 426: 50 x 1c 10 x 2c 2 x 5c 2 x 10c 
Combination 427: 50 x 1c 10 x 2c 2 x 5c 1 x 20c 
Combination 428: 50 x 1c 10 x 2c 3 x 10c 
Combination 429: 50 x 1c 10 x 2c 1 x 10c 1 x 20c 
Combination 430: 50 x 1c 5 x 2c 8 x 5c 
Combination 431: 50 x 1c 5 x 2c 6 x 5c 1 x 10c 
Combination 432: 50 x 1c 5 x 2c 4 x 5c 2 x 10c 
Combination 433: 50 x 1c 5 x 2c 4 x 5c 1 x 20c 
Combination 434: 50 x 1c 5 x 2c 2 x 5c 3 x 10c 
Combination 435: 50 x 1c 5 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 436: 50 x 1c 5 x 2c 4 x 10c 
Combination 437: 50 x 1c 5 x 2c 2 x 10c 1 x 20c 
Combination 438: 50 x 1c 5 x 2c 2 x 20c 
Combination 439: 50 x 1c 10 x 5c 
Combination 440: 50 x 1c 8 x 5c 1 x 10c 
Combination 441: 50 x 1c 6 x 5c 2 x 10c 
Combination 442: 50 x 1c 6 x 5c 1 x 20c 
Combination 443: 50 x 1c 4 x 5c 3 x 10c 
Combination 444: 50 x 1c 4 x 5c 1 x 10c 1 x 20c 
Combination 445: 50 x 1c 2 x 5c 4 x 10c 
Combination 446: 50 x 1c 2 x 5c 2 x 10c 1 x 20c 
Combination 447: 50 x 1c 2 x 5c 2 x 20c 
Combination 448: 50 x 1c 5 x 10c 
Combination 449: 50 x 1c 3 x 10c 1 x 20c 
Combination 450: 50 x 1c 1 x 10c 2 x 20c 
Combination 451: 50 x 1c 1 x 50c 
Combination 452: 49 x 1c 23 x 2c 1 x 5c 
Combination 453: 49 x 1c 18 x 2c 3 x 5c 
Combination 454: 49 x 1c 18 x 2c 1 x 5c 1 x 10c 
Combination 455: 49 x 1c 13 x 2c 5 x 5c 
Combination 456: 49 x 1c 13 x 2c 3 x 5c 1 x 10c 
Combination 457: 49 x 1c 13 x 2c 1 x 5c 2 x 10c 
Combination 458: 49 x 1c 13 x 2c 1 x 5c 1 x 20c 
Combination 459: 49 x 1c 8 x 2c 7 x 5c 
Combination 460: 49 x 1c 8 x 2c 5 x 5c 1 x 10c 
Combination 461: 49 x 1c 8 x 2c 3 x 5c 2 x 10c 
Combination 462: 49 x 1c 8 x 2c 3 x 5c 1 x 20c 
Combination 463: 49 x 1c 8 x 2c 1 x 5c 3 x 10c 
Combination 464: 49 x 1c 8 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 465: 49 x 1c 3 x 2c 9 x 5c 
Combination 466: 49 x 1c 3 x 2c 7 x 5c 1 x 10c 
Combination 467: 49 x 1c 3 x 2c 5 x 5c 2 x 10c 
Combination 468: 49 x 1c 3 x 2c 5 x 5c 1 x 20c 
Combination 469: 49 x 1c 3 x 2c 3 x 5c 3 x 10c 
Combination 470: 49 x 1c 3 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 471: 49 x 1c 3 x 2c 1 x 5c 4 x 10c 
Combination 472: 49 x 1c 3 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 473: 49 x 1c 3 x 2c 1 x 5c 2 x 20c 
Combination 474: 48 x 1c 26 x 2c 
Combination 475: 48 x 1c 21 x 2c 2 x 5c 
Combination 476: 48 x 1c 21 x 2c 1 x 10c 
Combination 477: 48 x 1c 16 x 2c 4 x 5c 
Combination 478: 48 x 1c 16 x 2c 2 x 5c 1 x 10c 
Combination 479: 48 x 1c 16 x 2c 2 x 10c 
Combination 480: 48 x 1c 16 x 2c 1 x 20c 
Combination 481: 48 x 1c 11 x 2c 6 x 5c 
Combination 482: 48 x 1c 11 x 2c 4 x 5c 1 x 10c 
Combination 483: 48 x 1c 11 x 2c 2 x 5c 2 x 10c 
Combination 484: 48 x 1c 11 x 2c 2 x 5c 1 x 20c 
Combination 485: 48 x 1c 11 x 2c 3 x 10c 
Combination 486: 48 x 1c 11 x 2c 1 x 10c 1 x 20c 
Combination 487: 48 x 1c 6 x 2c 8 x 5c 
Combination 488: 48 x 1c 6 x 2c 6 x 5c 1 x 10c 
Combination 489: 48 x 1c 6 x 2c 4 x 5c 2 x 10c 
Combination 490: 48 x 1c 6 x 2c 4 x 5c 1 x 20c 
Combination 491: 48 x 1c 6 x 2c 2 x 5c 3 x 10c 
Combination 492: 48 x 1c 6 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 493: 48 x 1c 6 x 2c 4 x 10c 
Combination 494: 48 x 1c 6 x 2c 2 x 10c 1 x 20c 
Combination 495: 48 x 1c 6 x 2c 2 x 20c 
Combination 496: 48 x 1c 1 x 2c 10 x 5c 
Combination 497: 48 x 1c 1 x 2c 8 x 5c 1 x 10c 
Combination 498: 48 x 1c 1 x 2c 6 x 5c 2 x 10c 
Combination 499: 48 x 1c 1 x 2c 6 x 5c 1 x 20c 
Combination 500: 48 x 1c 1 x 2c 4 x 5c 3 x 10c 
Combination 501: 48 x 1c 1 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 502: 48 x 1c 1 x 2c 2 x 5c 4 x 10c 
Combination 503: 48 x 1c 1 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 504: 48 x 1c 1 x 2c 2 x 5c 2 x 20c 
Combination 505: 48 x 1c 1 x 2c 5 x 10c 
Combination 506: 48 x 1c 1 x 2c 3 x 10c 1 x 20c 
Combination 507: 48 x 1c 1 x 2c 1 x 10c 2 x 20c 
Combination 508: 48 x 1c 1 x 2c 1 x 50c 
Combination 509: 47 x 1c 24 x 2c 1 x 5c 
Combination 510: 47 x 1c 19 x 2c 3 x 5c 
Combination 511: 47 x 1c 19 x 2c 1 x 5c 1 x 10c 
Combination 512: 47 x 1c 14 x 2c 5 x 5c 
Combination 513: 47 x 1c 14 x 2c 3 x 5c 1 x 10c 
Combination 514: 47 x 1c 14 x 2c 1 x 5c 2 x 10c 
Combination 515: 47 x 1c 14 x 2c 1 x 5c 1 x 20c 
Combination 516: 47 x 1c 9 x 2c 7 x 5c 
Combination 517: 47 x 1c 9 x 2c 5 x 5c 1 x 10c 
Combination 518: 47 x 1c 9 x 2c 3 x 5c 2 x 10c 
Combination 519: 47 x 1c 9 x 2c 3 x 5c 1 x 20c 
Combination 520: 47 x 1c 9 x 2c 1 x 5c 3 x 10c 
Combination 521: 47 x 1c 9 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 522: 47 x 1c 4 x 2c 9 x 5c 
Combination 523: 47 x 1c 4 x 2c 7 x 5c 1 x 10c 
Combination 524: 47 x 1c 4 x 2c 5 x 5c 2 x 10c 
Combination 525: 47 x 1c 4 x 2c 5 x 5c 1 x 20c 
Combination 526: 47 x 1c 4 x 2c 3 x 5c 3 x 10c 
Combination 527: 47 x 1c 4 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 528: 47 x 1c 4 x 2c 1 x 5c 4 x 10c 
Combination 529: 47 x 1c 4 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 530: 47 x 1c 4 x 2c 1 x 5c 2 x 20c 
Combination 531: 46 x 1c 27 x 2c 
Combination 532: 46 x 1c 22 x 2c 2 x 5c 
Combination 533: 46 x 1c 22 x 2c 1 x 10c 
Combination 534: 46 x 1c 17 x 2c 4 x 5c 
Combination 535: 46 x 1c 17 x 2c 2 x 5c 1 x 10c 
Combination 536: 46 x 1c 17 x 2c 2 x 10c 
Combination 537: 46 x 1c 17 x 2c 1 x 20c 
Combination 538: 46 x 1c 12 x 2c 6 x 5c 
Combination 539: 46 x 1c 12 x 2c 4 x 5c 1 x 10c 
Combination 540: 46 x 1c 12 x 2c 2 x 5c 2 x 10c 
Combination 541: 46 x 1c 12 x 2c 2 x 5c 1 x 20c 
Combination 542: 46 x 1c 12 x 2c 3 x 10c 
Combination 543: 46 x 1c 12 x 2c 1 x 10c 1 x 20c 
Combination 544: 46 x 1c 7 x 2c 8 x 5c 
Combination 545: 46 x 1c 7 x 2c 6 x 5c 1 x 10c 
Combination 546: 46 x 1c 7 x 2c 4 x 5c 2 x 10c 
Combination 547: 46 x 1c 7 x 2c 4 x 5c 1 x 20c 
Combination 548: 46 x 1c 7 x 2c 2 x 5c 3 x 10c 
Combination 549: 46 x 1c 7 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 550: 46 x 1c 7 x 2c 4 x 10c 
Combination 551: 46 x 1c 7 x 2c 2 x 10c 1 x 20c 
Combination 552: 46 x 1c 7 x 2c 2 x 20c 
Combination 553: 46 x 1c 2 x 2c 10 x 5c 
Combination 554: 46 x 1c 2 x 2c 8 x 5c 1 x 10c 
Combination 555: 46 x 1c 2 x 2c 6 x 5c 2 x 10c 
Combination 556: 46 x 1c 2 x 2c 6 x 5c 1 x 20c 
Combination 557: 46 x 1c 2 x 2c 4 x 5c 3 x 10c 
Combination 558: 46 x 1c 2 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 559: 46 x 1c 2 x 2c 2 x 5c 4 x 10c 
Combination 560: 46 x 1c 2 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 561: 46 x 1c 2 x 2c 2 x 5c 2 x 20c 
Combination 562: 46 x 1c 2 x 2c 5 x 10c 
Combination 563: 46 x 1c 2 x 2c 3 x 10c 1 x 20c 
Combination 564: 46 x 1c 2 x 2c 1 x 10c 2 x 20c 
Combination 565: 46 x 1c 2 x 2c 1 x 50c 
Combination 566: 45 x 1c 25 x 2c 1 x 5c 
Combination 567: 45 x 1c 20 x 2c 3 x 5c 
Combination 568: 45 x 1c 20 x 2c 1 x 5c 1 x 10c 
Combination 569: 45 x 1c 15 x 2c 5 x 5c 
Combination 570: 45 x 1c 15 x 2c 3 x 5c 1 x 10c 
Combination 571: 45 x 1c 15 x 2c 1 x 5c 2 x 10c 
Combination 572: 45 x 1c 15 x 2c 1 x 5c 1 x 20c 
Combination 573: 45 x 1c 10 x 2c 7 x 5c 
Combination 574: 45 x 1c 10 x 2c 5 x 5c 1 x 10c 
Combination 575: 45 x 1c 10 x 2c 3 x 5c 2 x 10c 
Combination 576: 45 x 1c 10 x 2c 3 x 5c 1 x 20c 
Combination 577: 45 x 1c 10 x 2c 1 x 5c 3 x 10c 
Combination 578: 45 x 1c 10 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 579: 45 x 1c 5 x 2c 9 x 5c 
Combination 580: 45 x 1c 5 x 2c 7 x 5c 1 x 10c 
Combination 581: 45 x 1c 5 x 2c 5 x 5c 2 x 10c 
Combination 582: 45 x 1c 5 x 2c 5 x 5c 1 x 20c 
Combination 583: 45 x 1c 5 x 2c 3 x 5c 3 x 10c 
Combination 584: 45 x 1c 5 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 585: 45 x 1c 5 x 2c 1 x 5c 4 x 10c 
Combination 586: 45 x 1c 5 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 587: 45 x 1c 5 x 2c 1 x 5c 2 x 20c 
Combination 588: 45 x 1c 11 x 5c 
Combination 589: 45 x 1c 9 x 5c 1 x 10c 
Combination 590: 45 x 1c 7 x 5c 2 x 10c 
Combination 591: 45 x 1c 7 x 5c 1 x 20c 
Combination 592: 45 x 1c 5 x 5c 3 x 10c 
Combination 593: 45 x 1c 5 x 5c 1 x 10c 1 x 20c 
Combination 594: 45 x 1c 3 x 5c 4 x 10c 
Combination 595: 45 x 1c 3 x 5c 2 x 10c 1 x 20c 
Combination 596: 45 x 1c 3 x 5c 2 x 20c 
Combination 597: 45 x 1c 1 x 5c 5 x 10c 
Combination 598: 45 x 1c 1 x 5c 3 x 10c 1 x 20c 
Combination 599: 45 x 1c 1 x 5c 1 x 10c 2 x 20c 
Combination 600: 45 x 1c 1 x 5c 1 x 50c 
Combination 601: 44 x 1c 28 x 2c 
Combination 602: 44 x 1c 23 x 2c 2 x 5c 
Combination 603: 44 x 1c 23 x 2c 1 x 10c 
Combination 604: 44 x 1c 18 x 2c 4 x 5c 
Combination 605: 44 x 1c 18 x 2c 2 x 5c 1 x 10c 
Combination 606: 44 x 1c 18 x 2c 2 x 10c 
Combination 607: 44 x 1c 18 x 2c 1 x 20c 
Combination 608: 44 x 1c 13 x 2c 6 x 5c 
Combination 609: 44 x 1c 13 x 2c 4 x 5c 1 x 10c 
Combination 610: 44 x 1c 13 x 2c 2 x 5c 2 x 10c 
Combination 611: 44 x 1c 13 x 2c 2 x 5c 1 x 20c 
Combination 612: 44 x 1c 13 x 2c 3 x 10c 
Combination 613: 44 x 1c 13 x 2c 1 x 10c 1 x 20c 
Combination 614: 44 x 1c 8 x 2c 8 x 5c 
Combination 615: 44 x 1c 8 x 2c 6 x 5c 1 x 10c 
Combination 616: 44 x 1c 8 x 2c 4 x 5c 2 x 10c 
Combination 617: 44 x 1c 8 x 2c 4 x 5c 1 x 20c 
Combination 618: 44 x 1c 8 x 2c 2 x 5c 3 x 10c 
Combination 619: 44 x 1c 8 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 620: 44 x 1c 8 x 2c 4 x 10c 
Combination 621: 44 x 1c 8 x 2c 2 x 10c 1 x 20c 
Combination 622: 44 x 1c 8 x 2c 2 x 20c 
Combination 623: 44 x 1c 3 x 2c 10 x 5c 
Combination 624: 44 x 1c 3 x 2c 8 x 5c 1 x 10c 
Combination 625: 44 x 1c 3 x 2c 6 x 5c 2 x 10c 
Combination 626: 44 x 1c 3 x 2c 6 x 5c 1 x 20c 
Combination 627: 44 x 1c 3 x 2c 4 x 5c 3 x 10c 
Combination 628: 44 x 1c 3 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 629: 44 x 1c 3 x 2c 2 x 5c 4 x 10c 
Combination 630: 44 x 1c 3 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 631: 44 x 1c 3 x 2c 2 x 5c 2 x 20c 
Combination 632: 44 x 1c 3 x 2c 5 x 10c 
Combination 633: 44 x 1c 3 x 2c 3 x 10c 1 x 20c 
Combination 634: 44 x 1c 3 x 2c 1 x 10c 2 x 20c 
Combination 635: 44 x 1c 3 x 2c 1 x 50c 
Combination 636: 43 x 1c 26 x 2c 1 x 5c 
Combination 637: 43 x 1c 21 x 2c 3 x 5c 
Combination 638: 43 x 1c 21 x 2c 1 x 5c 1 x 10c 
Combination 639: 43 x 1c 16 x 2c 5 x 5c 
Combination 640: 43 x 1c 16 x 2c 3 x 5c 1 x 10c 
Combination 641: 43 x 1c 16 x 2c 1 x 5c 2 x 10c 
Combination 642: 43 x 1c 16 x 2c 1 x 5c 1 x 20c 
Combination 643: 43 x 1c 11 x 2c 7 x 5c 
Combination 644: 43 x 1c 11 x 2c 5 x 5c 1 x 10c 
Combination 645: 43 x 1c 11 x 2c 3 x 5c 2 x 10c 
Combination 646: 43 x 1c 11 x 2c 3 x 5c 1 x 20c 
Combination 647: 43 x 1c 11 x 2c 1 x 5c 3 x 10c 
Combination 648: 43 x 1c 11 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 649: 43 x 1c 6 x 2c 9 x 5c 
Combination 650: 43 x 1c 6 x 2c 7 x 5c 1 x 10c 
Combination 651: 43 x 1c 6 x 2c 5 x 5c 2 x 10c 
Combination 652: 43 x 1c 6 x 2c 5 x 5c 1 x 20c 
Combination 653: 43 x 1c 6 x 2c 3 x 5c 3 x 10c 
Combination 654: 43 x 1c 6 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 655: 43 x 1c 6 x 2c 1 x 5c 4 x 10c 
Combination 656: 43 x 1c 6 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 657: 43 x 1c 6 x 2c 1 x 5c 2 x 20c 
Combination 658: 43 x 1c 1 x 2c 11 x 5c 
Combination 659: 43 x 1c 1 x 2c 9 x 5c 1 x 10c 
Combination 660: 43 x 1c 1 x 2c 7 x 5c 2 x 10c 
Combination 661: 43 x 1c 1 x 2c 7 x 5c 1 x 20c 
Combination 662: 43 x 1c 1 x 2c 5 x 5c 3 x 10c 
Combination 663: 43 x 1c 1 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 664: 43 x 1c 1 x 2c 3 x 5c 4 x 10c 
Combination 665: 43 x 1c 1 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 666: 43 x 1c 1 x 2c 3 x 5c 2 x 20c 
Combination 667: 43 x 1c 1 x 2c 1 x 5c 5 x 10c 
Combination 668: 43 x 1c 1 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 669: 43 x 1c 1 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 670: 43 x 1c 1 x 2c 1 x 5c 1 x 50c 
Combination 671: 42 x 1c 29 x 2c 
Combination 672: 42 x 1c 24 x 2c 2 x 5c 
Combination 673: 42 x 1c 24 x 2c 1 x 10c 
Combination 674: 42 x 1c 19 x 2c 4 x 5c 
Combination 675: 42 x 1c 19 x 2c 2 x 5c 1 x 10c 
Combination 676: 42 x 1c 19 x 2c 2 x 10c 
Combination 677: 42 x 1c 19 x 2c 1 x 20c 
Combination 678: 42 x 1c 14 x 2c 6 x 5c 
Combination 679: 42 x 1c 14 x 2c 4 x 5c 1 x 10c 
Combination 680: 42 x 1c 14 x 2c 2 x 5c 2 x 10c 
Combination 681: 42 x 1c 14 x 2c 2 x 5c 1 x 20c 
Combination 682: 42 x 1c 14 x 2c 3 x 10c 
Combination 683: 42 x 1c 14 x 2c 1 x 10c 1 x 20c 
Combination 684: 42 x 1c 9 x 2c 8 x 5c 
Combination 685: 42 x 1c 9 x 2c 6 x 5c 1 x 10c 
Combination 686: 42 x 1c 9 x 2c 4 x 5c 2 x 10c 
Combination 687: 42 x 1c 9 x 2c 4 x 5c 1 x 20c 
Combination 688: 42 x 1c 9 x 2c 2 x 5c 3 x 10c 
Combination 689: 42 x 1c 9 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 690: 42 x 1c 9 x 2c 4 x 10c 
Combination 691: 42 x 1c 9 x 2c 2 x 10c 1 x 20c 
Combination 692: 42 x 1c 9 x 2c 2 x 20c 
Combination 693: 42 x 1c 4 x 2c 10 x 5c 
Combination 694: 42 x 1c 4 x 2c 8 x 5c 1 x 10c 
Combination 695: 42 x 1c 4 x 2c 6 x 5c 2 x 10c 
Combination 696: 42 x 1c 4 x 2c 6 x 5c 1 x 20c 
Combination 697: 42 x 1c 4 x 2c 4 x 5c 3 x 10c 
Combination 698: 42 x 1c 4 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 699: 42 x 1c 4 x 2c 2 x 5c 4 x 10c 
Combination 700: 42 x 1c 4 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 701: 42 x 1c 4 x 2c 2 x 5c 2 x 20c 
Combination 702: 42 x 1c 4 x 2c 5 x 10c 
Combination 703: 42 x 1c 4 x 2c 3 x 10c 1 x 20c 
Combination 704: 42 x 1c 4 x 2c 1 x 10c 2 x 20c 
Combination 705: 42 x 1c 4 x 2c 1 x 50c 
Combination 706: 41 x 1c 27 x 2c 1 x 5c 
Combination 707: 41 x 1c 22 x 2c 3 x 5c 
Combination 708: 41 x 1c 22 x 2c 1 x 5c 1 x 10c 
Combination 709: 41 x 1c 17 x 2c 5 x 5c 
Combination 710: 41 x 1c 17 x 2c 3 x 5c 1 x 10c 
Combination 711: 41 x 1c 17 x 2c 1 x 5c 2 x 10c 
Combination 712: 41 x 1c 17 x 2c 1 x 5c 1 x 20c 
Combination 713: 41 x 1c 12 x 2c 7 x 5c 
Combination 714: 41 x 1c 12 x 2c 5 x 5c 1 x 10c 
Combination 715: 41 x 1c 12 x 2c 3 x 5c 2 x 10c 
Combination 716: 41 x 1c 12 x 2c 3 x 5c 1 x 20c 
Combination 717: 41 x 1c 12 x 2c 1 x 5c 3 x 10c 
Combination 718: 41 x 1c 12 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 719: 41 x 1c 7 x 2c 9 x 5c 
Combination 720: 41 x 1c 7 x 2c 7 x 5c 1 x 10c 
Combination 721: 41 x 1c 7 x 2c 5 x 5c 2 x 10c 
Combination 722: 41 x 1c 7 x 2c 5 x 5c 1 x 20c 
Combination 723: 41 x 1c 7 x 2c 3 x 5c 3 x 10c 
Combination 724: 41 x 1c 7 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 725: 41 x 1c 7 x 2c 1 x 5c 4 x 10c 
Combination 726: 41 x 1c 7 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 727: 41 x 1c 7 x 2c 1 x 5c 2 x 20c 
Combination 728: 41 x 1c 2 x 2c 11 x 5c 
Combination 729: 41 x 1c 2 x 2c 9 x 5c 1 x 10c 
Combination 730: 41 x 1c 2 x 2c 7 x 5c 2 x 10c 
Combination 731: 41 x 1c 2 x 2c 7 x 5c 1 x 20c 
Combination 732: 41 x 1c 2 x 2c 5 x 5c 3 x 10c 
Combination 733: 41 x 1c 2 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 734: 41 x 1c 2 x 2c 3 x 5c 4 x 10c 
Combination 735: 41 x 1c 2 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 736: 41 x 1c 2 x 2c 3 x 5c 2 x 20c 
Combination 737: 41 x 1c 2 x 2c 1 x 5c 5 x 10c 
Combination 738: 41 x 1c 2 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 739: 41 x 1c 2 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 740: 41 x 1c 2 x 2c 1 x 5c 1 x 50c 
Combination 741: 40 x 1c 30 x 2c 
Combination 742: 40 x 1c 25 x 2c 2 x 5c 
Combination 743: 40 x 1c 25 x 2c 1 x 10c 
Combination 744: 40 x 1c 20 x 2c 4 x 5c 
Combination 745: 40 x 1c 20 x 2c 2 x 5c 1 x 10c 
Combination 746: 40 x 1c 20 x 2c 2 x 10c 
Combination 747: 40 x 1c 20 x 2c 1 x 20c 
Combination 748: 40 x 1c 15 x 2c 6 x 5c 
Combination 749: 40 x 1c 15 x 2c 4 x 5c 1 x 10c 
Combination 750: 40 x 1c 15 x 2c 2 x 5c 2 x 10c 
Combination 751: 40 x 1c 15 x 2c 2 x 5c 1 x 20c 
Combination 752: 40 x 1c 15 x 2c 3 x 10c 
Combination 753: 40 x 1c 15 x 2c 1 x 10c 1 x 20c 
Combination 754: 40 x 1c 10 x 2c 8 x 5c 
Combination 755: 40 x 1c 10 x 2c 6 x 5c 1 x 10c 
Combination 756: 40 x 1c 10 x 2c 4 x 5c 2 x 10c 
Combination 757: 40 x 1c 10 x 2c 4 x 5c 1 x 20c 
Combination 758: 40 x 1c 10 x 2c 2 x 5c 3 x 10c 
Combination 759: 40 x 1c 10 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 760: 40 x 1c 10 x 2c 4 x 10c 
Combination 761: 40 x 1c 10 x 2c 2 x 10c 1 x 20c 
Combination 762: 40 x 1c 10 x 2c 2 x 20c 
Combination 763: 40 x 1c 5 x 2c 10 x 5c 
Combination 764: 40 x 1c 5 x 2c 8 x 5c 1 x 10c 
Combination 765: 40 x 1c 5 x 2c 6 x 5c 2 x 10c 
Combination 766: 40 x 1c 5 x 2c 6 x 5c 1 x 20c 
Combination 767: 40 x 1c 5 x 2c 4 x 5c 3 x 10c 
Combination 768: 40 x 1c 5 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 769: 40 x 1c 5 x 2c 2 x 5c 4 x 10c 
Combination 770: 40 x 1c 5 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 771: 40 x 1c 5 x 2c 2 x 5c 2 x 20c 
Combination 772: 40 x 1c 5 x 2c 5 x 10c 
Combination 773: 40 x 1c 5 x 2c 3 x 10c 1 x 20c 
Combination 774: 40 x 1c 5 x 2c 1 x 10c 2 x 20c 
Combination 775: 40 x 1c 5 x 2c 1 x 50c 
Combination 776: 40 x 1c 12 x 5c 
Combination 777: 40 x 1c 10 x 5c 1 x 10c 
Combination 778: 40 x 1c 8 x 5c 2 x 10c 
Combination 779: 40 x 1c 8 x 5c 1 x 20c 
Combination 780: 40 x 1c 6 x 5c 3 x 10c 
Combination 781: 40 x 1c 6 x 5c 1 x 10c 1 x 20c 
Combination 782: 40 x 1c 4 x 5c 4 x 10c 
Combination 783: 40 x 1c 4 x 5c 2 x 10c 1 x 20c 
Combination 784: 40 x 1c 4 x 5c 2 x 20c 
Combination 785: 40 x 1c 2 x 5c 5 x 10c 
Combination 786: 40 x 1c 2 x 5c 3 x 10c 1 x 20c 
Combination 787: 40 x 1c 2 x 5c 1 x 10c 2 x 20c 
Combination 788: 40 x 1c 2 x 5c 1 x 50c 
Combination 789: 40 x 1c 6 x 10c 
Combination 790: 40 x 1c 4 x 10c 1 x 20c 
Combination 791: 40 x 1c 2 x 10c 2 x 20c 
Combination 792: 40 x 1c 1 x 10c 1 x 50c 
Combination 793: 40 x 1c 3 x 20c 
Combination 794: 39 x 1c 28 x 2c 1 x 5c 
Combination 795: 39 x 1c 23 x 2c 3 x 5c 
Combination 796: 39 x 1c 23 x 2c 1 x 5c 1 x 10c 
Combination 797: 39 x 1c 18 x 2c 5 x 5c 
Combination 798: 39 x 1c 18 x 2c 3 x 5c 1 x 10c 
Combination 799: 39 x 1c 18 x 2c 1 x 5c 2 x 10c 
Combination 800: 39 x 1c 18 x 2c 1 x 5c 1 x 20c 
Combination 801: 39 x 1c 13 x 2c 7 x 5c 
Combination 802: 39 x 1c 13 x 2c 5 x 5c 1 x 10c 
Combination 803: 39 x 1c 13 x 2c 3 x 5c 2 x 10c 
Combination 804: 39 x 1c 13 x 2c 3 x 5c 1 x 20c 
Combination 805: 39 x 1c 13 x 2c 1 x 5c 3 x 10c 
Combination 806: 39 x 1c 13 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 807: 39 x 1c 8 x 2c 9 x 5c 
Combination 808: 39 x 1c 8 x 2c 7 x 5c 1 x 10c 
Combination 809: 39 x 1c 8 x 2c 5 x 5c 2 x 10c 
Combination 810: 39 x 1c 8 x 2c 5 x 5c 1 x 20c 
Combination 811: 39 x 1c 8 x 2c 3 x 5c 3 x 10c 
Combination 812: 39 x 1c 8 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 813: 39 x 1c 8 x 2c 1 x 5c 4 x 10c 
Combination 814: 39 x 1c 8 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 815: 39 x 1c 8 x 2c 1 x 5c 2 x 20c 
Combination 816: 39 x 1c 3 x 2c 11 x 5c 
Combination 817: 39 x 1c 3 x 2c 9 x 5c 1 x 10c 
Combination 818: 39 x 1c 3 x 2c 7 x 5c 2 x 10c 
Combination 819: 39 x 1c 3 x 2c 7 x 5c 1 x 20c 
Combination 820: 39 x 1c 3 x 2c 5 x 5c 3 x 10c 
Combination 821: 39 x 1c 3 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 822: 39 x 1c 3 x 2c 3 x 5c 4 x 10c 
Combination 823: 39 x 1c 3 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 824: 39 x 1c 3 x 2c 3 x 5c 2 x 20c 
Combination 825: 39 x 1c 3 x 2c 1 x 5c 5 x 10c 
Combination 826: 39 x 1c 3 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 827: 39 x 1c 3 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 828: 39 x 1c 3 x 2c 1 x 5c 1 x 50c 
Combination 829: 38 x 1c 31 x 2c 
Combination 830: 38 x 1c 26 x 2c 2 x 5c 
Combination 831: 38 x 1c 26 x 2c 1 x 10c 
Combination 832: 38 x 1c 21 x 2c 4 x 5c 
Combination 833: 38 x 1c 21 x 2c 2 x 5c 1 x 10c 
Combination 834: 38 x 1c 21 x 2c 2 x 10c 
Combination 835: 38 x 1c 21 x 2c 1 x 20c 
Combination 836: 38 x 1c 16 x 2c 6 x 5c 
Combination 837: 38 x 1c 16 x 2c 4 x 5c 1 x 10c 
Combination 838: 38 x 1c 16 x 2c 2 x 5c 2 x 10c 
Combination 839: 38 x 1c 16 x 2c 2 x 5c 1 x 20c 
Combination 840: 38 x 1c 16 x 2c 3 x 10c 
Combination 841: 38 x 1c 16 x 2c 1 x 10c 1 x 20c 
Combination 842: 38 x 1c 11 x 2c 8 x 5c 
Combination 843: 38 x 1c 11 x 2c 6 x 5c 1 x 10c 
Combination 844: 38 x 1c 11 x 2c 4 x 5c 2 x 10c 
Combination 845: 38 x 1c 11 x 2c 4 x 5c 1 x 20c 
Combination 846: 38 x 1c 11 x 2c 2 x 5c 3 x 10c 
Combination 847: 38 x 1c 11 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 848: 38 x 1c 11 x 2c 4 x 10c 
Combination 849: 38 x 1c 11 x 2c 2 x 10c 1 x 20c 
Combination 850: 38 x 1c 11 x 2c 2 x 20c 
Combination 851: 38 x 1c 6 x 2c 10 x 5c 
Combination 852: 38 x 1c 6 x 2c 8 x 5c 1 x 10c 
Combination 853: 38 x 1c 6 x 2c 6 x 5c 2 x 10c 
Combination 854: 38 x 1c 6 x 2c 6 x 5c 1 x 20c 
Combination 855: 38 x 1c 6 x 2c 4 x 5c 3 x 10c 
Combination 856: 38 x 1c 6 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 857: 38 x 1c 6 x 2c 2 x 5c 4 x 10c 
Combination 858: 38 x 1c 6 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 859: 38 x 1c 6 x 2c 2 x 5c 2 x 20c 
Combination 860: 38 x 1c 6 x 2c 5 x 10c 
Combination 861: 38 x 1c 6 x 2c 3 x 10c 1 x 20c 
Combination 862: 38 x 1c 6 x 2c 1 x 10c 2 x 20c 
Combination 863: 38 x 1c 6 x 2c 1 x 50c 
Combination 864: 38 x 1c 1 x 2c 12 x 5c 
Combination 865: 38 x 1c 1 x 2c 10 x 5c 1 x 10c 
Combination 866: 38 x 1c 1 x 2c 8 x 5c 2 x 10c 
Combination 867: 38 x 1c 1 x 2c 8 x 5c 1 x 20c 
Combination 868: 38 x 1c 1 x 2c 6 x 5c 3 x 10c 
Combination 869: 38 x 1c 1 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 870: 38 x 1c 1 x 2c 4 x 5c 4 x 10c 
Combination 871: 38 x 1c 1 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 872: 38 x 1c 1 x 2c 4 x 5c 2 x 20c 
Combination 873: 38 x 1c 1 x 2c 2 x 5c 5 x 10c 
Combination 874: 38 x 1c 1 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 875: 38 x 1c 1 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 876: 38 x 1c 1 x 2c 2 x 5c 1 x 50c 
Combination 877: 38 x 1c 1 x 2c 6 x 10c 
Combination 878: 38 x 1c 1 x 2c 4 x 10c 1 x 20c 
Combination 879: 38 x 1c 1 x 2c 2 x 10c 2 x 20c 
Combination 880: 38 x 1c 1 x 2c 1 x 10c 1 x 50c 
Combination 881: 38 x 1c 1 x 2c 3 x 20c 
Combination 882: 37 x 1c 29 x 2c 1 x 5c 
Combination 883: 37 x 1c 24 x 2c 3 x 5c 
Combination 884: 37 x 1c 24 x 2c 1 x 5c 1 x 10c 
Combination 885: 37 x 1c 19 x 2c 5 x 5c 
Combination 886: 37 x 1c 19 x 2c 3 x 5c 1 x 10c 
Combination 887: 37 x 1c 19 x 2c 1 x 5c 2 x 10c 
Combination 888: 37 x 1c 19 x 2c 1 x 5c 1 x 20c 
Combination 889: 37 x 1c 14 x 2c 7 x 5c 
Combination 890: 37 x 1c 14 x 2c 5 x 5c 1 x 10c 
Combination 891: 37 x 1c 14 x 2c 3 x 5c 2 x 10c 
Combination 892: 37 x 1c 14 x 2c 3 x 5c 1 x 20c 
Combination 893: 37 x 1c 14 x 2c 1 x 5c 3 x 10c 
Combination 894: 37 x 1c 14 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 895: 37 x 1c 9 x 2c 9 x 5c 
Combination 896: 37 x 1c 9 x 2c 7 x 5c 1 x 10c 
Combination 897: 37 x 1c 9 x 2c 5 x 5c 2 x 10c 
Combination 898: 37 x 1c 9 x 2c 5 x 5c 1 x 20c 
Combination 899: 37 x 1c 9 x 2c 3 x 5c 3 x 10c 
Combination 900: 37 x 1c 9 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 901: 37 x 1c 9 x 2c 1 x 5c 4 x 10c 
Combination 902: 37 x 1c 9 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 903: 37 x 1c 9 x 2c 1 x 5c 2 x 20c 
Combination 904: 37 x 1c 4 x 2c 11 x 5c 
Combination 905: 37 x 1c 4 x 2c 9 x 5c 1 x 10c 
Combination 906: 37 x 1c 4 x 2c 7 x 5c 2 x 10c 
Combination 907: 37 x 1c 4 x 2c 7 x 5c 1 x 20c 
Combination 908: 37 x 1c 4 x 2c 5 x 5c 3 x 10c 
Combination 909: 37 x 1c 4 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 910: 37 x 1c 4 x 2c 3 x 5c 4 x 10c 
Combination 911: 37 x 1c 4 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 912: 37 x 1c 4 x 2c 3 x 5c 2 x 20c 
Combination 913: 37 x 1c 4 x 2c 1 x 5c 5 x 10c 
Combination 914: 37 x 1c 4 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 915: 37 x 1c 4 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 916: 37 x 1c 4 x 2c 1 x 5c 1 x 50c 
Combination 917: 36 x 1c 32 x 2c 
Combination 918: 36 x 1c 27 x 2c 2 x 5c 
Combination 919: 36 x 1c 27 x 2c 1 x 10c 
Combination 920: 36 x 1c 22 x 2c 4 x 5c 
Combination 921: 36 x 1c 22 x 2c 2 x 5c 1 x 10c 
Combination 922: 36 x 1c 22 x 2c 2 x 10c 
Combination 923: 36 x 1c 22 x 2c 1 x 20c 
Combination 924: 36 x 1c 17 x 2c 6 x 5c 
Combination 925: 36 x 1c 17 x 2c 4 x 5c 1 x 10c 
Combination 926: 36 x 1c 17 x 2c 2 x 5c 2 x 10c 
Combination 927: 36 x 1c 17 x 2c 2 x 5c 1 x 20c 
Combination 928: 36 x 1c 17 x 2c 3 x 10c 
Combination 929: 36 x 1c 17 x 2c 1 x 10c 1 x 20c 
Combination 930: 36 x 1c 12 x 2c 8 x 5c 
Combination 931: 36 x 1c 12 x 2c 6 x 5c 1 x 10c 
Combination 932: 36 x 1c 12 x 2c 4 x 5c 2 x 10c 
Combination 933: 36 x 1c 12 x 2c 4 x 5c 1 x 20c 
Combination 934: 36 x 1c 12 x 2c 2 x 5c 3 x 10c 
Combination 935: 36 x 1c 12 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 936: 36 x 1c 12 x 2c 4 x 10c 
Combination 937: 36 x 1c 12 x 2c 2 x 10c 1 x 20c 
Combination 938: 36 x 1c 12 x 2c 2 x 20c 
Combination 939: 36 x 1c 7 x 2c 10 x 5c 
Combination 940: 36 x 1c 7 x 2c 8 x 5c 1 x 10c 
Combination 941: 36 x 1c 7 x 2c 6 x 5c 2 x 10c 
Combination 942: 36 x 1c 7 x 2c 6 x 5c 1 x 20c 
Combination 943: 36 x 1c 7 x 2c 4 x 5c 3 x 10c 
Combination 944: 36 x 1c 7 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 945: 36 x 1c 7 x 2c 2 x 5c 4 x 10c 
Combination 946: 36 x 1c 7 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 947: 36 x 1c 7 x 2c 2 x 5c 2 x 20c 
Combination 948: 36 x 1c 7 x 2c 5 x 10c 
Combination 949: 36 x 1c 7 x 2c 3 x 10c 1 x 20c 
Combination 950: 36 x 1c 7 x 2c 1 x 10c 2 x 20c 
Combination 951: 36 x 1c 7 x 2c 1 x 50c 
Combination 952: 36 x 1c 2 x 2c 12 x 5c 
Combination 953: 36 x 1c 2 x 2c 10 x 5c 1 x 10c 
Combination 954: 36 x 1c 2 x 2c 8 x 5c 2 x 10c 
Combination 955: 36 x 1c 2 x 2c 8 x 5c 1 x 20c 
Combination 956: 36 x 1c 2 x 2c 6 x 5c 3 x 10c 
Combination 957: 36 x 1c 2 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 958: 36 x 1c 2 x 2c 4 x 5c 4 x 10c 
Combination 959: 36 x 1c 2 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 960: 36 x 1c 2 x 2c 4 x 5c 2 x 20c 
Combination 961: 36 x 1c 2 x 2c 2 x 5c 5 x 10c 
Combination 962: 36 x 1c 2 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 963: 36 x 1c 2 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 964: 36 x 1c 2 x 2c 2 x 5c 1 x 50c 
Combination 965: 36 x 1c 2 x 2c 6 x 10c 
Combination 966: 36 x 1c 2 x 2c 4 x 10c 1 x 20c 
Combination 967: 36 x 1c 2 x 2c 2 x 10c 2 x 20c 
Combination 968: 36 x 1c 2 x 2c 1 x 10c 1 x 50c 
Combination 969: 36 x 1c 2 x 2c 3 x 20c 
Combination 970: 35 x 1c 30 x 2c 1 x 5c 
Combination 971: 35 x 1c 25 x 2c 3 x 5c 
Combination 972: 35 x 1c 25 x 2c 1 x 5c 1 x 10c 
Combination 973: 35 x 1c 20 x 2c 5 x 5c 
Combination 974: 35 x 1c 20 x 2c 3 x 5c 1 x 10c 
Combination 975: 35 x 1c 20 x 2c 1 x 5c 2 x 10c 
Combination 976: 35 x 1c 20 x 2c 1 x 5c 1 x 20c 
Combination 977: 35 x 1c 15 x 2c 7 x 5c 
Combination 978: 35 x 1c 15 x 2c 5 x 5c 1 x 10c 
Combination 979: 35 x 1c 15 x 2c 3 x 5c 2 x 10c 
Combination 980: 35 x 1c 15 x 2c 3 x 5c 1 x 20c 
Combination 981: 35 x 1c 15 x 2c 1 x 5c 3 x 10c 
Combination 982: 35 x 1c 15 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 983: 35 x 1c 10 x 2c 9 x 5c 
Combination 984: 35 x 1c 10 x 2c 7 x 5c 1 x 10c 
Combination 985: 35 x 1c 10 x 2c 5 x 5c 2 x 10c 
Combination 986: 35 x 1c 10 x 2c 5 x 5c 1 x 20c 
Combination 987: 35 x 1c 10 x 2c 3 x 5c 3 x 10c 
Combination 988: 35 x 1c 10 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 989: 35 x 1c 10 x 2c 1 x 5c 4 x 10c 
Combination 990: 35 x 1c 10 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 991: 35 x 1c 10 x 2c 1 x 5c 2 x 20c 
Combination 992: 35 x 1c 5 x 2c 11 x 5c 
Combination 993: 35 x 1c 5 x 2c 9 x 5c 1 x 10c 
Combination 994: 35 x 1c 5 x 2c 7 x 5c 2 x 10c 
Combination 995: 35 x 1c 5 x 2c 7 x 5c 1 x 20c 
Combination 996: 35 x 1c 5 x 2c 5 x 5c 3 x 10c 
Combination 997: 35 x 1c 5 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 998: 35 x 1c 5 x 2c 3 x 5c 4 x 10c 
Combination 999: 35 x 1c 5 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1000: 35 x 1c 5 x 2c 3 x 5c 2 x 20c 
Combination 1001: 35 x 1c 5 x 2c 1 x 5c 5 x 10c 
Combination 1002: 35 x 1c 5 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1003: 35 x 1c 5 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1004: 35 x 1c 5 x 2c 1 x 5c 1 x 50c 
Combination 1005: 35 x 1c 13 x 5c 
Combination 1006: 35 x 1c 11 x 5c 1 x 10c 
Combination 1007: 35 x 1c 9 x 5c 2 x 10c 
Combination 1008: 35 x 1c 9 x 5c 1 x 20c 
Combination 1009: 35 x 1c 7 x 5c 3 x 10c 
Combination 1010: 35 x 1c 7 x 5c 1 x 10c 1 x 20c 
Combination 1011: 35 x 1c 5 x 5c 4 x 10c 
Combination 1012: 35 x 1c 5 x 5c 2 x 10c 1 x 20c 
Combination 1013: 35 x 1c 5 x 5c 2 x 20c 
Combination 1014: 35 x 1c 3 x 5c 5 x 10c 
Combination 1015: 35 x 1c 3 x 5c 3 x 10c 1 x 20c 
Combination 1016: 35 x 1c 3 x 5c 1 x 10c 2 x 20c 
Combination 1017: 35 x 1c 3 x 5c 1 x 50c 
Combination 1018: 35 x 1c 1 x 5c 6 x 10c 
Combination 1019: 35 x 1c 1 x 5c 4 x 10c 1 x 20c 
Combination 1020: 35 x 1c 1 x 5c 2 x 10c 2 x 20c 
Combination 1021: 35 x 1c 1 x 5c 1 x 10c 1 x 50c 
Combination 1022: 35 x 1c 1 x 5c 3 x 20c 
Combination 1023: 34 x 1c 33 x 2c 
Combination 1024: 34 x 1c 28 x 2c 2 x 5c 
Combination 1025: 34 x 1c 28 x 2c 1 x 10c 
Combination 1026: 34 x 1c 23 x 2c 4 x 5c 
Combination 1027: 34 x 1c 23 x 2c 2 x 5c 1 x 10c 
Combination 1028: 34 x 1c 23 x 2c 2 x 10c 
Combination 1029: 34 x 1c 23 x 2c 1 x 20c 
Combination 1030: 34 x 1c 18 x 2c 6 x 5c 
Combination 1031: 34 x 1c 18 x 2c 4 x 5c 1 x 10c 
Combination 1032: 34 x 1c 18 x 2c 2 x 5c 2 x 10c 
Combination 1033: 34 x 1c 18 x 2c 2 x 5c 1 x 20c 
Combination 1034: 34 x 1c 18 x 2c 3 x 10c 
Combination 1035: 34 x 1c 18 x 2c 1 x 10c 1 x 20c 
Combination 1036: 34 x 1c 13 x 2c 8 x 5c 
Combination 1037: 34 x 1c 13 x 2c 6 x 5c 1 x 10c 
Combination 1038: 34 x 1c 13 x 2c 4 x 5c 2 x 10c 
Combination 1039: 34 x 1c 13 x 2c 4 x 5c 1 x 20c 
Combination 1040: 34 x 1c 13 x 2c 2 x 5c 3 x 10c 
Combination 1041: 34 x 1c 13 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1042: 34 x 1c 13 x 2c 4 x 10c 
Combination 1043: 34 x 1c 13 x 2c 2 x 10c 1 x 20c 
Combination 1044: 34 x 1c 13 x 2c 2 x 20c 
Combination 1045: 34 x 1c 8 x 2c 10 x 5c 
Combination 1046: 34 x 1c 8 x 2c 8 x 5c 1 x 10c 
Combination 1047: 34 x 1c 8 x 2c 6 x 5c 2 x 10c 
Combination 1048: 34 x 1c 8 x 2c 6 x 5c 1 x 20c 
Combination 1049: 34 x 1c 8 x 2c 4 x 5c 3 x 10c 
Combination 1050: 34 x 1c 8 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1051: 34 x 1c 8 x 2c 2 x 5c 4 x 10c 
Combination 1052: 34 x 1c 8 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1053: 34 x 1c 8 x 2c 2 x 5c 2 x 20c 
Combination 1054: 34 x 1c 8 x 2c 5 x 10c 
Combination 1055: 34 x 1c 8 x 2c 3 x 10c 1 x 20c 
Combination 1056: 34 x 1c 8 x 2c 1 x 10c 2 x 20c 
Combination 1057: 34 x 1c 8 x 2c 1 x 50c 
Combination 1058: 34 x 1c 3 x 2c 12 x 5c 
Combination 1059: 34 x 1c 3 x 2c 10 x 5c 1 x 10c 
Combination 1060: 34 x 1c 3 x 2c 8 x 5c 2 x 10c 
Combination 1061: 34 x 1c 3 x 2c 8 x 5c 1 x 20c 
Combination 1062: 34 x 1c 3 x 2c 6 x 5c 3 x 10c 
Combination 1063: 34 x 1c 3 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1064: 34 x 1c 3 x 2c 4 x 5c 4 x 10c 
Combination 1065: 34 x 1c 3 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1066: 34 x 1c 3 x 2c 4 x 5c 2 x 20c 
Combination 1067: 34 x 1c 3 x 2c 2 x 5c 5 x 10c 
Combination 1068: 34 x 1c 3 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1069: 34 x 1c 3 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1070: 34 x 1c 3 x 2c 2 x 5c 1 x 50c 
Combination 1071: 34 x 1c 3 x 2c 6 x 10c 
Combination 1072: 34 x 1c 3 x 2c 4 x 10c 1 x 20c 
Combination 1073: 34 x 1c 3 x 2c 2 x 10c 2 x 20c 
Combination 1074: 34 x 1c 3 x 2c 1 x 10c 1 x 50c 
Combination 1075: 34 x 1c 3 x 2c 3 x 20c 
Combination 1076: 33 x 1c 31 x 2c 1 x 5c 
Combination 1077: 33 x 1c 26 x 2c 3 x 5c 
Combination 1078: 33 x 1c 26 x 2c 1 x 5c 1 x 10c 
Combination 1079: 33 x 1c 21 x 2c 5 x 5c 
Combination 1080: 33 x 1c 21 x 2c 3 x 5c 1 x 10c 
Combination 1081: 33 x 1c 21 x 2c 1 x 5c 2 x 10c 
Combination 1082: 33 x 1c 21 x 2c 1 x 5c 1 x 20c 
Combination 1083: 33 x 1c 16 x 2c 7 x 5c 
Combination 1084: 33 x 1c 16 x 2c 5 x 5c 1 x 10c 
Combination 1085: 33 x 1c 16 x 2c 3 x 5c 2 x 10c 
Combination 1086: 33 x 1c 16 x 2c 3 x 5c 1 x 20c 
Combination 1087: 33 x 1c 16 x 2c 1 x 5c 3 x 10c 
Combination 1088: 33 x 1c 16 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1089: 33 x 1c 11 x 2c 9 x 5c 
Combination 1090: 33 x 1c 11 x 2c 7 x 5c 1 x 10c 
Combination 1091: 33 x 1c 11 x 2c 5 x 5c 2 x 10c 
Combination 1092: 33 x 1c 11 x 2c 5 x 5c 1 x 20c 
Combination 1093: 33 x 1c 11 x 2c 3 x 5c 3 x 10c 
Combination 1094: 33 x 1c 11 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1095: 33 x 1c 11 x 2c 1 x 5c 4 x 10c 
Combination 1096: 33 x 1c 11 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1097: 33 x 1c 11 x 2c 1 x 5c 2 x 20c 
Combination 1098: 33 x 1c 6 x 2c 11 x 5c 
Combination 1099: 33 x 1c 6 x 2c 9 x 5c 1 x 10c 
Combination 1100: 33 x 1c 6 x 2c 7 x 5c 2 x 10c 
Combination 1101: 33 x 1c 6 x 2c 7 x 5c 1 x 20c 
Combination 1102: 33 x 1c 6 x 2c 5 x 5c 3 x 10c 
Combination 1103: 33 x 1c 6 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1104: 33 x 1c 6 x 2c 3 x 5c 4 x 10c 
Combination 1105: 33 x 1c 6 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1106: 33 x 1c 6 x 2c 3 x 5c 2 x 20c 
Combination 1107: 33 x 1c 6 x 2c 1 x 5c 5 x 10c 
Combination 1108: 33 x 1c 6 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1109: 33 x 1c 6 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1110: 33 x 1c 6 x 2c 1 x 5c 1 x 50c 
Combination 1111: 33 x 1c 1 x 2c 13 x 5c 
Combination 1112: 33 x 1c 1 x 2c 11 x 5c 1 x 10c 
Combination 1113: 33 x 1c 1 x 2c 9 x 5c 2 x 10c 
Combination 1114: 33 x 1c 1 x 2c 9 x 5c 1 x 20c 
Combination 1115: 33 x 1c 1 x 2c 7 x 5c 3 x 10c 
Combination 1116: 33 x 1c 1 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1117: 33 x 1c 1 x 2c 5 x 5c 4 x 10c 
Combination 1118: 33 x 1c 1 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1119: 33 x 1c 1 x 2c 5 x 5c 2 x 20c 
Combination 1120: 33 x 1c 1 x 2c 3 x 5c 5 x 10c 
Combination 1121: 33 x 1c 1 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1122: 33 x 1c 1 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1123: 33 x 1c 1 x 2c 3 x 5c 1 x 50c 
Combination 1124: 33 x 1c 1 x 2c 1 x 5c 6 x 10c 
Combination 1125: 33 x 1c 1 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1126: 33 x 1c 1 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1127: 33 x 1c 1 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1128: 33 x 1c 1 x 2c 1 x 5c 3 x 20c 
Combination 1129: 32 x 1c 34 x 2c 
Combination 1130: 32 x 1c 29 x 2c 2 x 5c 
Combination 1131: 32 x 1c 29 x 2c 1 x 10c 
Combination 1132: 32 x 1c 24 x 2c 4 x 5c 
Combination 1133: 32 x 1c 24 x 2c 2 x 5c 1 x 10c 
Combination 1134: 32 x 1c 24 x 2c 2 x 10c 
Combination 1135: 32 x 1c 24 x 2c 1 x 20c 
Combination 1136: 32 x 1c 19 x 2c 6 x 5c 
Combination 1137: 32 x 1c 19 x 2c 4 x 5c 1 x 10c 
Combination 1138: 32 x 1c 19 x 2c 2 x 5c 2 x 10c 
Combination 1139: 32 x 1c 19 x 2c 2 x 5c 1 x 20c 
Combination 1140: 32 x 1c 19 x 2c 3 x 10c 
Combination 1141: 32 x 1c 19 x 2c 1 x 10c 1 x 20c 
Combination 1142: 32 x 1c 14 x 2c 8 x 5c 
Combination 1143: 32 x 1c 14 x 2c 6 x 5c 1 x 10c 
Combination 1144: 32 x 1c 14 x 2c 4 x 5c 2 x 10c 
Combination 1145: 32 x 1c 14 x 2c 4 x 5c 1 x 20c 
Combination 1146: 32 x 1c 14 x 2c 2 x 5c 3 x 10c 
Combination 1147: 32 x 1c 14 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1148: 32 x 1c 14 x 2c 4 x 10c 
Combination 1149: 32 x 1c 14 x 2c 2 x 10c 1 x 20c 
Combination 1150: 32 x 1c 14 x 2c 2 x 20c 
Combination 1151: 32 x 1c 9 x 2c 10 x 5c 
Combination 1152: 32 x 1c 9 x 2c 8 x 5c 1 x 10c 
Combination 1153: 32 x 1c 9 x 2c 6 x 5c 2 x 10c 
Combination 1154: 32 x 1c 9 x 2c 6 x 5c 1 x 20c 
Combination 1155: 32 x 1c 9 x 2c 4 x 5c 3 x 10c 
Combination 1156: 32 x 1c 9 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1157: 32 x 1c 9 x 2c 2 x 5c 4 x 10c 
Combination 1158: 32 x 1c 9 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1159: 32 x 1c 9 x 2c 2 x 5c 2 x 20c 
Combination 1160: 32 x 1c 9 x 2c 5 x 10c 
Combination 1161: 32 x 1c 9 x 2c 3 x 10c 1 x 20c 
Combination 1162: 32 x 1c 9 x 2c 1 x 10c 2 x 20c 
Combination 1163: 32 x 1c 9 x 2c 1 x 50c 
Combination 1164: 32 x 1c 4 x 2c 12 x 5c 
Combination 1165: 32 x 1c 4 x 2c 10 x 5c 1 x 10c 
Combination 1166: 32 x 1c 4 x 2c 8 x 5c 2 x 10c 
Combination 1167: 32 x 1c 4 x 2c 8 x 5c 1 x 20c 
Combination 1168: 32 x 1c 4 x 2c 6 x 5c 3 x 10c 
Combination 1169: 32 x 1c 4 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1170: 32 x 1c 4 x 2c 4 x 5c 4 x 10c 
Combination 1171: 32 x 1c 4 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1172: 32 x 1c 4 x 2c 4 x 5c 2 x 20c 
Combination 1173: 32 x 1c 4 x 2c 2 x 5c 5 x 10c 
Combination 1174: 32 x 1c 4 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1175: 32 x 1c 4 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1176: 32 x 1c 4 x 2c 2 x 5c 1 x 50c 
Combination 1177: 32 x 1c 4 x 2c 6 x 10c 
Combination 1178: 32 x 1c 4 x 2c 4 x 10c 1 x 20c 
Combination 1179: 32 x 1c 4 x 2c 2 x 10c 2 x 20c 
Combination 1180: 32 x 1c 4 x 2c 1 x 10c 1 x 50c 
Combination 1181: 32 x 1c 4 x 2c 3 x 20c 
Combination 1182: 31 x 1c 32 x 2c 1 x 5c 
Combination 1183: 31 x 1c 27 x 2c 3 x 5c 
Combination 1184: 31 x 1c 27 x 2c 1 x 5c 1 x 10c 
Combination 1185: 31 x 1c 22 x 2c 5 x 5c 
Combination 1186: 31 x 1c 22 x 2c 3 x 5c 1 x 10c 
Combination 1187: 31 x 1c 22 x 2c 1 x 5c 2 x 10c 
Combination 1188: 31 x 1c 22 x 2c 1 x 5c 1 x 20c 
Combination 1189: 31 x 1c 17 x 2c 7 x 5c 
Combination 1190: 31 x 1c 17 x 2c 5 x 5c 1 x 10c 
Combination 1191: 31 x 1c 17 x 2c 3 x 5c 2 x 10c 
Combination 1192: 31 x 1c 17 x 2c 3 x 5c 1 x 20c 
Combination 1193: 31 x 1c 17 x 2c 1 x 5c 3 x 10c 
Combination 1194: 31 x 1c 17 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1195: 31 x 1c 12 x 2c 9 x 5c 
Combination 1196: 31 x 1c 12 x 2c 7 x 5c 1 x 10c 
Combination 1197: 31 x 1c 12 x 2c 5 x 5c 2 x 10c 
Combination 1198: 31 x 1c 12 x 2c 5 x 5c 1 x 20c 
Combination 1199: 31 x 1c 12 x 2c 3 x 5c 3 x 10c 
Combination 1200: 31 x 1c 12 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1201: 31 x 1c 12 x 2c 1 x 5c 4 x 10c 
Combination 1202: 31 x 1c 12 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1203: 31 x 1c 12 x 2c 1 x 5c 2 x 20c 
Combination 1204: 31 x 1c 7 x 2c 11 x 5c 
Combination 1205: 31 x 1c 7 x 2c 9 x 5c 1 x 10c 
Combination 1206: 31 x 1c 7 x 2c 7 x 5c 2 x 10c 
Combination 1207: 31 x 1c 7 x 2c 7 x 5c 1 x 20c 
Combination 1208: 31 x 1c 7 x 2c 5 x 5c 3 x 10c 
Combination 1209: 31 x 1c 7 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1210: 31 x 1c 7 x 2c 3 x 5c 4 x 10c 
Combination 1211: 31 x 1c 7 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1212: 31 x 1c 7 x 2c 3 x 5c 2 x 20c 
Combination 1213: 31 x 1c 7 x 2c 1 x 5c 5 x 10c 
Combination 1214: 31 x 1c 7 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1215: 31 x 1c 7 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1216: 31 x 1c 7 x 2c 1 x 5c 1 x 50c 
Combination 1217: 31 x 1c 2 x 2c 13 x 5c 
Combination 1218: 31 x 1c 2 x 2c 11 x 5c 1 x 10c 
Combination 1219: 31 x 1c 2 x 2c 9 x 5c 2 x 10c 
Combination 1220: 31 x 1c 2 x 2c 9 x 5c 1 x 20c 
Combination 1221: 31 x 1c 2 x 2c 7 x 5c 3 x 10c 
Combination 1222: 31 x 1c 2 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1223: 31 x 1c 2 x 2c 5 x 5c 4 x 10c 
Combination 1224: 31 x 1c 2 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1225: 31 x 1c 2 x 2c 5 x 5c 2 x 20c 
Combination 1226: 31 x 1c 2 x 2c 3 x 5c 5 x 10c 
Combination 1227: 31 x 1c 2 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1228: 31 x 1c 2 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1229: 31 x 1c 2 x 2c 3 x 5c 1 x 50c 
Combination 1230: 31 x 1c 2 x 2c 1 x 5c 6 x 10c 
Combination 1231: 31 x 1c 2 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1232: 31 x 1c 2 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1233: 31 x 1c 2 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1234: 31 x 1c 2 x 2c 1 x 5c 3 x 20c 
Combination 1235: 30 x 1c 35 x 2c 
Combination 1236: 30 x 1c 30 x 2c 2 x 5c 
Combination 1237: 30 x 1c 30 x 2c 1 x 10c 
Combination 1238: 30 x 1c 25 x 2c 4 x 5c 
Combination 1239: 30 x 1c 25 x 2c 2 x 5c 1 x 10c 
Combination 1240: 30 x 1c 25 x 2c 2 x 10c 
Combination 1241: 30 x 1c 25 x 2c 1 x 20c 
Combination 1242: 30 x 1c 20 x 2c 6 x 5c 
Combination 1243: 30 x 1c 20 x 2c 4 x 5c 1 x 10c 
Combination 1244: 30 x 1c 20 x 2c 2 x 5c 2 x 10c 
Combination 1245: 30 x 1c 20 x 2c 2 x 5c 1 x 20c 
Combination 1246: 30 x 1c 20 x 2c 3 x 10c 
Combination 1247: 30 x 1c 20 x 2c 1 x 10c 1 x 20c 
Combination 1248: 30 x 1c 15 x 2c 8 x 5c 
Combination 1249: 30 x 1c 15 x 2c 6 x 5c 1 x 10c 
Combination 1250: 30 x 1c 15 x 2c 4 x 5c 2 x 10c 
Combination 1251: 30 x 1c 15 x 2c 4 x 5c 1 x 20c 
Combination 1252: 30 x 1c 15 x 2c 2 x 5c 3 x 10c 
Combination 1253: 30 x 1c 15 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1254: 30 x 1c 15 x 2c 4 x 10c 
Combination 1255: 30 x 1c 15 x 2c 2 x 10c 1 x 20c 
Combination 1256: 30 x 1c 15 x 2c 2 x 20c 
Combination 1257: 30 x 1c 10 x 2c 10 x 5c 
Combination 1258: 30 x 1c 10 x 2c 8 x 5c 1 x 10c 
Combination 1259: 30 x 1c 10 x 2c 6 x 5c 2 x 10c 
Combination 1260: 30 x 1c 10 x 2c 6 x 5c 1 x 20c 
Combination 1261: 30 x 1c 10 x 2c 4 x 5c 3 x 10c 
Combination 1262: 30 x 1c 10 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1263: 30 x 1c 10 x 2c 2 x 5c 4 x 10c 
Combination 1264: 30 x 1c 10 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1265: 30 x 1c 10 x 2c 2 x 5c 2 x 20c 
Combination 1266: 30 x 1c 10 x 2c 5 x 10c 
Combination 1267: 30 x 1c 10 x 2c 3 x 10c 1 x 20c 
Combination 1268: 30 x 1c 10 x 2c 1 x 10c 2 x 20c 
Combination 1269: 30 x 1c 10 x 2c 1 x 50c 
Combination 1270: 30 x 1c 5 x 2c 12 x 5c 
Combination 1271: 30 x 1c 5 x 2c 10 x 5c 1 x 10c 
Combination 1272: 30 x 1c 5 x 2c 8 x 5c 2 x 10c 
Combination 1273: 30 x 1c 5 x 2c 8 x 5c 1 x 20c 
Combination 1274: 30 x 1c 5 x 2c 6 x 5c 3 x 10c 
Combination 1275: 30 x 1c 5 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1276: 30 x 1c 5 x 2c 4 x 5c 4 x 10c 
Combination 1277: 30 x 1c 5 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1278: 30 x 1c 5 x 2c 4 x 5c 2 x 20c 
Combination 1279: 30 x 1c 5 x 2c 2 x 5c 5 x 10c 
Combination 1280: 30 x 1c 5 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1281: 30 x 1c 5 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1282: 30 x 1c 5 x 2c 2 x 5c 1 x 50c 
Combination 1283: 30 x 1c 5 x 2c 6 x 10c 
Combination 1284: 30 x 1c 5 x 2c 4 x 10c 1 x 20c 
Combination 1285: 30 x 1c 5 x 2c 2 x 10c 2 x 20c 
Combination 1286: 30 x 1c 5 x 2c 1 x 10c 1 x 50c 
Combination 1287: 30 x 1c 5 x 2c 3 x 20c 
Combination 1288: 30 x 1c 14 x 5c 
Combination 1289: 30 x 1c 12 x 5c 1 x 10c 
Combination 1290: 30 x 1c 10 x 5c 2 x 10c 
Combination 1291: 30 x 1c 10 x 5c 1 x 20c 
Combination 1292: 30 x 1c 8 x 5c 3 x 10c 
Combination 1293: 30 x 1c 8 x 5c 1 x 10c 1 x 20c 
Combination 1294: 30 x 1c 6 x 5c 4 x 10c 
Combination 1295: 30 x 1c 6 x 5c 2 x 10c 1 x 20c 
Combination 1296: 30 x 1c 6 x 5c 2 x 20c 
Combination 1297: 30 x 1c 4 x 5c 5 x 10c 
Combination 1298: 30 x 1c 4 x 5c 3 x 10c 1 x 20c 
Combination 1299: 30 x 1c 4 x 5c 1 x 10c 2 x 20c 
Combination 1300: 30 x 1c 4 x 5c 1 x 50c 
Combination 1301: 30 x 1c 2 x 5c 6 x 10c 
Combination 1302: 30 x 1c 2 x 5c 4 x 10c 1 x 20c 
Combination 1303: 30 x 1c 2 x 5c 2 x 10c 2 x 20c 
Combination 1304: 30 x 1c 2 x 5c 1 x 10c 1 x 50c 
Combination 1305: 30 x 1c 2 x 5c 3 x 20c 
Combination 1306: 30 x 1c 7 x 10c 
Combination 1307: 30 x 1c 5 x 10c 1 x 20c 
Combination 1308: 30 x 1c 3 x 10c 2 x 20c 
Combination 1309: 30 x 1c 2 x 10c 1 x 50c 
Combination 1310: 30 x 1c 1 x 10c 3 x 20c 
Combination 1311: 30 x 1c 1 x 20c 1 x 50c 
Combination 1312: 29 x 1c 33 x 2c 1 x 5c 
Combination 1313: 29 x 1c 28 x 2c 3 x 5c 
Combination 1314: 29 x 1c 28 x 2c 1 x 5c 1 x 10c 
Combination 1315: 29 x 1c 23 x 2c 5 x 5c 
Combination 1316: 29 x 1c 23 x 2c 3 x 5c 1 x 10c 
Combination 1317: 29 x 1c 23 x 2c 1 x 5c 2 x 10c 
Combination 1318: 29 x 1c 23 x 2c 1 x 5c 1 x 20c 
Combination 1319: 29 x 1c 18 x 2c 7 x 5c 
Combination 1320: 29 x 1c 18 x 2c 5 x 5c 1 x 10c 
Combination 1321: 29 x 1c 18 x 2c 3 x 5c 2 x 10c 
Combination 1322: 29 x 1c 18 x 2c 3 x 5c 1 x 20c 
Combination 1323: 29 x 1c 18 x 2c 1 x 5c 3 x 10c 
Combination 1324: 29 x 1c 18 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1325: 29 x 1c 13 x 2c 9 x 5c 
Combination 1326: 29 x 1c 13 x 2c 7 x 5c 1 x 10c 
Combination 1327: 29 x 1c 13 x 2c 5 x 5c 2 x 10c 
Combination 1328: 29 x 1c 13 x 2c 5 x 5c 1 x 20c 
Combination 1329: 29 x 1c 13 x 2c 3 x 5c 3 x 10c 
Combination 1330: 29 x 1c 13 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1331: 29 x 1c 13 x 2c 1 x 5c 4 x 10c 
Combination 1332: 29 x 1c 13 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1333: 29 x 1c 13 x 2c 1 x 5c 2 x 20c 
Combination 1334: 29 x 1c 8 x 2c 11 x 5c 
Combination 1335: 29 x 1c 8 x 2c 9 x 5c 1 x 10c 
Combination 1336: 29 x 1c 8 x 2c 7 x 5c 2 x 10c 
Combination 1337: 29 x 1c 8 x 2c 7 x 5c 1 x 20c 
Combination 1338: 29 x 1c 8 x 2c 5 x 5c 3 x 10c 
Combination 1339: 29 x 1c 8 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1340: 29 x 1c 8 x 2c 3 x 5c 4 x 10c 
Combination 1341: 29 x 1c 8 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1342: 29 x 1c 8 x 2c 3 x 5c 2 x 20c 
Combination 1343: 29 x 1c 8 x 2c 1 x 5c 5 x 10c 
Combination 1344: 29 x 1c 8 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1345: 29 x 1c 8 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1346: 29 x 1c 8 x 2c 1 x 5c 1 x 50c 
Combination 1347: 29 x 1c 3 x 2c 13 x 5c 
Combination 1348: 29 x 1c 3 x 2c 11 x 5c 1 x 10c 
Combination 1349: 29 x 1c 3 x 2c 9 x 5c 2 x 10c 
Combination 1350: 29 x 1c 3 x 2c 9 x 5c 1 x 20c 
Combination 1351: 29 x 1c 3 x 2c 7 x 5c 3 x 10c 
Combination 1352: 29 x 1c 3 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1353: 29 x 1c 3 x 2c 5 x 5c 4 x 10c 
Combination 1354: 29 x 1c 3 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1355: 29 x 1c 3 x 2c 5 x 5c 2 x 20c 
Combination 1356: 29 x 1c 3 x 2c 3 x 5c 5 x 10c 
Combination 1357: 29 x 1c 3 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1358: 29 x 1c 3 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1359: 29 x 1c 3 x 2c 3 x 5c 1 x 50c 
Combination 1360: 29 x 1c 3 x 2c 1 x 5c 6 x 10c 
Combination 1361: 29 x 1c 3 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1362: 29 x 1c 3 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1363: 29 x 1c 3 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1364: 29 x 1c 3 x 2c 1 x 5c 3 x 20c 
Combination 1365: 28 x 1c 36 x 2c 
Combination 1366: 28 x 1c 31 x 2c 2 x 5c 
Combination 1367: 28 x 1c 31 x 2c 1 x 10c 
Combination 1368: 28 x 1c 26 x 2c 4 x 5c 
Combination 1369: 28 x 1c 26 x 2c 2 x 5c 1 x 10c 
Combination 1370: 28 x 1c 26 x 2c 2 x 10c 
Combination 1371: 28 x 1c 26 x 2c 1 x 20c 
Combination 1372: 28 x 1c 21 x 2c 6 x 5c 
Combination 1373: 28 x 1c 21 x 2c 4 x 5c 1 x 10c 
Combination 1374: 28 x 1c 21 x 2c 2 x 5c 2 x 10c 
Combination 1375: 28 x 1c 21 x 2c 2 x 5c 1 x 20c 
Combination 1376: 28 x 1c 21 x 2c 3 x 10c 
Combination 1377: 28 x 1c 21 x 2c 1 x 10c 1 x 20c 
Combination 1378: 28 x 1c 16 x 2c 8 x 5c 
Combination 1379: 28 x 1c 16 x 2c 6 x 5c 1 x 10c 
Combination 1380: 28 x 1c 16 x 2c 4 x 5c 2 x 10c 
Combination 1381: 28 x 1c 16 x 2c 4 x 5c 1 x 20c 
Combination 1382: 28 x 1c 16 x 2c 2 x 5c 3 x 10c 
Combination 1383: 28 x 1c 16 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1384: 28 x 1c 16 x 2c 4 x 10c 
Combination 1385: 28 x 1c 16 x 2c 2 x 10c 1 x 20c 
Combination 1386: 28 x 1c 16 x 2c 2 x 20c 
Combination 1387: 28 x 1c 11 x 2c 10 x 5c 
Combination 1388: 28 x 1c 11 x 2c 8 x 5c 1 x 10c 
Combination 1389: 28 x 1c 11 x 2c 6 x 5c 2 x 10c 
Combination 1390: 28 x 1c 11 x 2c 6 x 5c 1 x 20c 
Combination 1391: 28 x 1c 11 x 2c 4 x 5c 3 x 10c 
Combination 1392: 28 x 1c 11 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1393: 28 x 1c 11 x 2c 2 x 5c 4 x 10c 
Combination 1394: 28 x 1c 11 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1395: 28 x 1c 11 x 2c 2 x 5c 2 x 20c 
Combination 1396: 28 x 1c 11 x 2c 5 x 10c 
Combination 1397: 28 x 1c 11 x 2c 3 x 10c 1 x 20c 
Combination 1398: 28 x 1c 11 x 2c 1 x 10c 2 x 20c 
Combination 1399: 28 x 1c 11 x 2c 1 x 50c 
Combination 1400: 28 x 1c 6 x 2c 12 x 5c 
Combination 1401: 28 x 1c 6 x 2c 10 x 5c 1 x 10c 
Combination 1402: 28 x 1c 6 x 2c 8 x 5c 2 x 10c 
Combination 1403: 28 x 1c 6 x 2c 8 x 5c 1 x 20c 
Combination 1404: 28 x 1c 6 x 2c 6 x 5c 3 x 10c 
Combination 1405: 28 x 1c 6 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1406: 28 x 1c 6 x 2c 4 x 5c 4 x 10c 
Combination 1407: 28 x 1c 6 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1408: 28 x 1c 6 x 2c 4 x 5c 2 x 20c 
Combination 1409: 28 x 1c 6 x 2c 2 x 5c 5 x 10c 
Combination 1410: 28 x 1c 6 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1411: 28 x 1c 6 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1412: 28 x 1c 6 x 2c 2 x 5c 1 x 50c 
Combination 1413: 28 x 1c 6 x 2c 6 x 10c 
Combination 1414: 28 x 1c 6 x 2c 4 x 10c 1 x 20c 
Combination 1415: 28 x 1c 6 x 2c 2 x 10c 2 x 20c 
Combination 1416: 28 x 1c 6 x 2c 1 x 10c 1 x 50c 
Combination 1417: 28 x 1c 6 x 2c 3 x 20c 
Combination 1418: 28 x 1c 1 x 2c 14 x 5c 
Combination 1419: 28 x 1c 1 x 2c 12 x 5c 1 x 10c 
Combination 1420: 28 x 1c 1 x 2c 10 x 5c 2 x 10c 
Combination 1421: 28 x 1c 1 x 2c 10 x 5c 1 x 20c 
Combination 1422: 28 x 1c 1 x 2c 8 x 5c 3 x 10c 
Combination 1423: 28 x 1c 1 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 1424: 28 x 1c 1 x 2c 6 x 5c 4 x 10c 
Combination 1425: 28 x 1c 1 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 1426: 28 x 1c 1 x 2c 6 x 5c 2 x 20c 
Combination 1427: 28 x 1c 1 x 2c 4 x 5c 5 x 10c 
Combination 1428: 28 x 1c 1 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 1429: 28 x 1c 1 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 1430: 28 x 1c 1 x 2c 4 x 5c 1 x 50c 
Combination 1431: 28 x 1c 1 x 2c 2 x 5c 6 x 10c 
Combination 1432: 28 x 1c 1 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 1433: 28 x 1c 1 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 1434: 28 x 1c 1 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 1435: 28 x 1c 1 x 2c 2 x 5c 3 x 20c 
Combination 1436: 28 x 1c 1 x 2c 7 x 10c 
Combination 1437: 28 x 1c 1 x 2c 5 x 10c 1 x 20c 
Combination 1438: 28 x 1c 1 x 2c 3 x 10c 2 x 20c 
Combination 1439: 28 x 1c 1 x 2c 2 x 10c 1 x 50c 
Combination 1440: 28 x 1c 1 x 2c 1 x 10c 3 x 20c 
Combination 1441: 28 x 1c 1 x 2c 1 x 20c 1 x 50c 
Combination 1442: 27 x 1c 34 x 2c 1 x 5c 
Combination 1443: 27 x 1c 29 x 2c 3 x 5c 
Combination 1444: 27 x 1c 29 x 2c 1 x 5c 1 x 10c 
Combination 1445: 27 x 1c 24 x 2c 5 x 5c 
Combination 1446: 27 x 1c 24 x 2c 3 x 5c 1 x 10c 
Combination 1447: 27 x 1c 24 x 2c 1 x 5c 2 x 10c 
Combination 1448: 27 x 1c 24 x 2c 1 x 5c 1 x 20c 
Combination 1449: 27 x 1c 19 x 2c 7 x 5c 
Combination 1450: 27 x 1c 19 x 2c 5 x 5c 1 x 10c 
Combination 1451: 27 x 1c 19 x 2c 3 x 5c 2 x 10c 
Combination 1452: 27 x 1c 19 x 2c 3 x 5c 1 x 20c 
Combination 1453: 27 x 1c 19 x 2c 1 x 5c 3 x 10c 
Combination 1454: 27 x 1c 19 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1455: 27 x 1c 14 x 2c 9 x 5c 
Combination 1456: 27 x 1c 14 x 2c 7 x 5c 1 x 10c 
Combination 1457: 27 x 1c 14 x 2c 5 x 5c 2 x 10c 
Combination 1458: 27 x 1c 14 x 2c 5 x 5c 1 x 20c 
Combination 1459: 27 x 1c 14 x 2c 3 x 5c 3 x 10c 
Combination 1460: 27 x 1c 14 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1461: 27 x 1c 14 x 2c 1 x 5c 4 x 10c 
Combination 1462: 27 x 1c 14 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1463: 27 x 1c 14 x 2c 1 x 5c 2 x 20c 
Combination 1464: 27 x 1c 9 x 2c 11 x 5c 
Combination 1465: 27 x 1c 9 x 2c 9 x 5c 1 x 10c 
Combination 1466: 27 x 1c 9 x 2c 7 x 5c 2 x 10c 
Combination 1467: 27 x 1c 9 x 2c 7 x 5c 1 x 20c 
Combination 1468: 27 x 1c 9 x 2c 5 x 5c 3 x 10c 
Combination 1469: 27 x 1c 9 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1470: 27 x 1c 9 x 2c 3 x 5c 4 x 10c 
Combination 1471: 27 x 1c 9 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1472: 27 x 1c 9 x 2c 3 x 5c 2 x 20c 
Combination 1473: 27 x 1c 9 x 2c 1 x 5c 5 x 10c 
Combination 1474: 27 x 1c 9 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1475: 27 x 1c 9 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1476: 27 x 1c 9 x 2c 1 x 5c 1 x 50c 
Combination 1477: 27 x 1c 4 x 2c 13 x 5c 
Combination 1478: 27 x 1c 4 x 2c 11 x 5c 1 x 10c 
Combination 1479: 27 x 1c 4 x 2c 9 x 5c 2 x 10c 
Combination 1480: 27 x 1c 4 x 2c 9 x 5c 1 x 20c 
Combination 1481: 27 x 1c 4 x 2c 7 x 5c 3 x 10c 
Combination 1482: 27 x 1c 4 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1483: 27 x 1c 4 x 2c 5 x 5c 4 x 10c 
Combination 1484: 27 x 1c 4 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1485: 27 x 1c 4 x 2c 5 x 5c 2 x 20c 
Combination 1486: 27 x 1c 4 x 2c 3 x 5c 5 x 10c 
Combination 1487: 27 x 1c 4 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1488: 27 x 1c 4 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1489: 27 x 1c 4 x 2c 3 x 5c 1 x 50c 
Combination 1490: 27 x 1c 4 x 2c 1 x 5c 6 x 10c 
Combination 1491: 27 x 1c 4 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1492: 27 x 1c 4 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1493: 27 x 1c 4 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1494: 27 x 1c 4 x 2c 1 x 5c 3 x 20c 
Combination 1495: 26 x 1c 37 x 2c 
Combination 1496: 26 x 1c 32 x 2c 2 x 5c 
Combination 1497: 26 x 1c 32 x 2c 1 x 10c 
Combination 1498: 26 x 1c 27 x 2c 4 x 5c 
Combination 1499: 26 x 1c 27 x 2c 2 x 5c 1 x 10c 
Combination 1500: 26 x 1c 27 x 2c 2 x 10c 
Combination 1501: 26 x 1c 27 x 2c 1 x 20c 
Combination 1502: 26 x 1c 22 x 2c 6 x 5c 
Combination 1503: 26 x 1c 22 x 2c 4 x 5c 1 x 10c 
Combination 1504: 26 x 1c 22 x 2c 2 x 5c 2 x 10c 
Combination 1505: 26 x 1c 22 x 2c 2 x 5c 1 x 20c 
Combination 1506: 26 x 1c 22 x 2c 3 x 10c 
Combination 1507: 26 x 1c 22 x 2c 1 x 10c 1 x 20c 
Combination 1508: 26 x 1c 17 x 2c 8 x 5c 
Combination 1509: 26 x 1c 17 x 2c 6 x 5c 1 x 10c 
Combination 1510: 26 x 1c 17 x 2c 4 x 5c 2 x 10c 
Combination 1511: 26 x 1c 17 x 2c 4 x 5c 1 x 20c 
Combination 1512: 26 x 1c 17 x 2c 2 x 5c 3 x 10c 
Combination 1513: 26 x 1c 17 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1514: 26 x 1c 17 x 2c 4 x 10c 
Combination 1515: 26 x 1c 17 x 2c 2 x 10c 1 x 20c 
Combination 1516: 26 x 1c 17 x 2c 2 x 20c 
Combination 1517: 26 x 1c 12 x 2c 10 x 5c 
Combination 1518: 26 x 1c 12 x 2c 8 x 5c 1 x 10c 
Combination 1519: 26 x 1c 12 x 2c 6 x 5c 2 x 10c 
Combination 1520: 26 x 1c 12 x 2c 6 x 5c 1 x 20c 
Combination 1521: 26 x 1c 12 x 2c 4 x 5c 3 x 10c 
Combination 1522: 26 x 1c 12 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1523: 26 x 1c 12 x 2c 2 x 5c 4 x 10c 
Combination 1524: 26 x 1c 12 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1525: 26 x 1c 12 x 2c 2 x 5c 2 x 20c 
Combination 1526: 26 x 1c 12 x 2c 5 x 10c 
Combination 1527: 26 x 1c 12 x 2c 3 x 10c 1 x 20c 
Combination 1528: 26 x 1c 12 x 2c 1 x 10c 2 x 20c 
Combination 1529: 26 x 1c 12 x 2c 1 x 50c 
Combination 1530: 26 x 1c 7 x 2c 12 x 5c 
Combination 1531: 26 x 1c 7 x 2c 10 x 5c 1 x 10c 
Combination 1532: 26 x 1c 7 x 2c 8 x 5c 2 x 10c 
Combination 1533: 26 x 1c 7 x 2c 8 x 5c 1 x 20c 
Combination 1534: 26 x 1c 7 x 2c 6 x 5c 3 x 10c 
Combination 1535: 26 x 1c 7 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1536: 26 x 1c 7 x 2c 4 x 5c 4 x 10c 
Combination 1537: 26 x 1c 7 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1538: 26 x 1c 7 x 2c 4 x 5c 2 x 20c 
Combination 1539: 26 x 1c 7 x 2c 2 x 5c 5 x 10c 
Combination 1540: 26 x 1c 7 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1541: 26 x 1c 7 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1542: 26 x 1c 7 x 2c 2 x 5c 1 x 50c 
Combination 1543: 26 x 1c 7 x 2c 6 x 10c 
Combination 1544: 26 x 1c 7 x 2c 4 x 10c 1 x 20c 
Combination 1545: 26 x 1c 7 x 2c 2 x 10c 2 x 20c 
Combination 1546: 26 x 1c 7 x 2c 1 x 10c 1 x 50c 
Combination 1547: 26 x 1c 7 x 2c 3 x 20c 
Combination 1548: 26 x 1c 2 x 2c 14 x 5c 
Combination 1549: 26 x 1c 2 x 2c 12 x 5c 1 x 10c 
Combination 1550: 26 x 1c 2 x 2c 10 x 5c 2 x 10c 
Combination 1551: 26 x 1c 2 x 2c 10 x 5c 1 x 20c 
Combination 1552: 26 x 1c 2 x 2c 8 x 5c 3 x 10c 
Combination 1553: 26 x 1c 2 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 1554: 26 x 1c 2 x 2c 6 x 5c 4 x 10c 
Combination 1555: 26 x 1c 2 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 1556: 26 x 1c 2 x 2c 6 x 5c 2 x 20c 
Combination 1557: 26 x 1c 2 x 2c 4 x 5c 5 x 10c 
Combination 1558: 26 x 1c 2 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 1559: 26 x 1c 2 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 1560: 26 x 1c 2 x 2c 4 x 5c 1 x 50c 
Combination 1561: 26 x 1c 2 x 2c 2 x 5c 6 x 10c 
Combination 1562: 26 x 1c 2 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 1563: 26 x 1c 2 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 1564: 26 x 1c 2 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 1565: 26 x 1c 2 x 2c 2 x 5c 3 x 20c 
Combination 1566: 26 x 1c 2 x 2c 7 x 10c 
Combination 1567: 26 x 1c 2 x 2c 5 x 10c 1 x 20c 
Combination 1568: 26 x 1c 2 x 2c 3 x 10c 2 x 20c 
Combination 1569: 26 x 1c 2 x 2c 2 x 10c 1 x 50c 
Combination 1570: 26 x 1c 2 x 2c 1 x 10c 3 x 20c 
Combination 1571: 26 x 1c 2 x 2c 1 x 20c 1 x 50c 
Combination 1572: 25 x 1c 35 x 2c 1 x 5c 
Combination 1573: 25 x 1c 30 x 2c 3 x 5c 
Combination 1574: 25 x 1c 30 x 2c 1 x 5c 1 x 10c 
Combination 1575: 25 x 1c 25 x 2c 5 x 5c 
Combination 1576: 25 x 1c 25 x 2c 3 x 5c 1 x 10c 
Combination 1577: 25 x 1c 25 x 2c 1 x 5c 2 x 10c 
Combination 1578: 25 x 1c 25 x 2c 1 x 5c 1 x 20c 
Combination 1579: 25 x 1c 20 x 2c 7 x 5c 
Combination 1580: 25 x 1c 20 x 2c 5 x 5c 1 x 10c 
Combination 1581: 25 x 1c 20 x 2c 3 x 5c 2 x 10c 
Combination 1582: 25 x 1c 20 x 2c 3 x 5c 1 x 20c 
Combination 1583: 25 x 1c 20 x 2c 1 x 5c 3 x 10c 
Combination 1584: 25 x 1c 20 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1585: 25 x 1c 15 x 2c 9 x 5c 
Combination 1586: 25 x 1c 15 x 2c 7 x 5c 1 x 10c 
Combination 1587: 25 x 1c 15 x 2c 5 x 5c 2 x 10c 
Combination 1588: 25 x 1c 15 x 2c 5 x 5c 1 x 20c 
Combination 1589: 25 x 1c 15 x 2c 3 x 5c 3 x 10c 
Combination 1590: 25 x 1c 15 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1591: 25 x 1c 15 x 2c 1 x 5c 4 x 10c 
Combination 1592: 25 x 1c 15 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1593: 25 x 1c 15 x 2c 1 x 5c 2 x 20c 
Combination 1594: 25 x 1c 10 x 2c 11 x 5c 
Combination 1595: 25 x 1c 10 x 2c 9 x 5c 1 x 10c 
Combination 1596: 25 x 1c 10 x 2c 7 x 5c 2 x 10c 
Combination 1597: 25 x 1c 10 x 2c 7 x 5c 1 x 20c 
Combination 1598: 25 x 1c 10 x 2c 5 x 5c 3 x 10c 
Combination 1599: 25 x 1c 10 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1600: 25 x 1c 10 x 2c 3 x 5c 4 x 10c 
Combination 1601: 25 x 1c 10 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1602: 25 x 1c 10 x 2c 3 x 5c 2 x 20c 
Combination 1603: 25 x 1c 10 x 2c 1 x 5c 5 x 10c 
Combination 1604: 25 x 1c 10 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1605: 25 x 1c 10 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1606: 25 x 1c 10 x 2c 1 x 5c 1 x 50c 
Combination 1607: 25 x 1c 5 x 2c 13 x 5c 
Combination 1608: 25 x 1c 5 x 2c 11 x 5c 1 x 10c 
Combination 1609: 25 x 1c 5 x 2c 9 x 5c 2 x 10c 
Combination 1610: 25 x 1c 5 x 2c 9 x 5c 1 x 20c 
Combination 1611: 25 x 1c 5 x 2c 7 x 5c 3 x 10c 
Combination 1612: 25 x 1c 5 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1613: 25 x 1c 5 x 2c 5 x 5c 4 x 10c 
Combination 1614: 25 x 1c 5 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1615: 25 x 1c 5 x 2c 5 x 5c 2 x 20c 
Combination 1616: 25 x 1c 5 x 2c 3 x 5c 5 x 10c 
Combination 1617: 25 x 1c 5 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1618: 25 x 1c 5 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1619: 25 x 1c 5 x 2c 3 x 5c 1 x 50c 
Combination 1620: 25 x 1c 5 x 2c 1 x 5c 6 x 10c 
Combination 1621: 25 x 1c 5 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1622: 25 x 1c 5 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1623: 25 x 1c 5 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1624: 25 x 1c 5 x 2c 1 x 5c 3 x 20c 
Combination 1625: 25 x 1c 15 x 5c 
Combination 1626: 25 x 1c 13 x 5c 1 x 10c 
Combination 1627: 25 x 1c 11 x 5c 2 x 10c 
Combination 1628: 25 x 1c 11 x 5c 1 x 20c 
Combination 1629: 25 x 1c 9 x 5c 3 x 10c 
Combination 1630: 25 x 1c 9 x 5c 1 x 10c 1 x 20c 
Combination 1631: 25 x 1c 7 x 5c 4 x 10c 
Combination 1632: 25 x 1c 7 x 5c 2 x 10c 1 x 20c 
Combination 1633: 25 x 1c 7 x 5c 2 x 20c 
Combination 1634: 25 x 1c 5 x 5c 5 x 10c 
Combination 1635: 25 x 1c 5 x 5c 3 x 10c 1 x 20c 
Combination 1636: 25 x 1c 5 x 5c 1 x 10c 2 x 20c 
Combination 1637: 25 x 1c 5 x 5c 1 x 50c 
Combination 1638: 25 x 1c 3 x 5c 6 x 10c 
Combination 1639: 25 x 1c 3 x 5c 4 x 10c 1 x 20c 
Combination 1640: 25 x 1c 3 x 5c 2 x 10c 2 x 20c 
Combination 1641: 25 x 1c 3 x 5c 1 x 10c 1 x 50c 
Combination 1642: 25 x 1c 3 x 5c 3 x 20c 
Combination 1643: 25 x 1c 1 x 5c 7 x 10c 
Combination 1644: 25 x 1c 1 x 5c 5 x 10c 1 x 20c 
Combination 1645: 25 x 1c 1 x 5c 3 x 10c 2 x 20c 
Combination 1646: 25 x 1c 1 x 5c 2 x 10c 1 x 50c 
Combination 1647: 25 x 1c 1 x 5c 1 x 10c 3 x 20c 
Combination 1648: 25 x 1c 1 x 5c 1 x 20c 1 x 50c 
Combination 1649: 24 x 1c 38 x 2c 
Combination 1650: 24 x 1c 33 x 2c 2 x 5c 
Combination 1651: 24 x 1c 33 x 2c 1 x 10c 
Combination 1652: 24 x 1c 28 x 2c 4 x 5c 
Combination 1653: 24 x 1c 28 x 2c 2 x 5c 1 x 10c 
Combination 1654: 24 x 1c 28 x 2c 2 x 10c 
Combination 1655: 24 x 1c 28 x 2c 1 x 20c 
Combination 1656: 24 x 1c 23 x 2c 6 x 5c 
Combination 1657: 24 x 1c 23 x 2c 4 x 5c 1 x 10c 
Combination 1658: 24 x 1c 23 x 2c 2 x 5c 2 x 10c 
Combination 1659: 24 x 1c 23 x 2c 2 x 5c 1 x 20c 
Combination 1660: 24 x 1c 23 x 2c 3 x 10c 
Combination 1661: 24 x 1c 23 x 2c 1 x 10c 1 x 20c 
Combination 1662: 24 x 1c 18 x 2c 8 x 5c 
Combination 1663: 24 x 1c 18 x 2c 6 x 5c 1 x 10c 
Combination 1664: 24 x 1c 18 x 2c 4 x 5c 2 x 10c 
Combination 1665: 24 x 1c 18 x 2c 4 x 5c 1 x 20c 
Combination 1666: 24 x 1c 18 x 2c 2 x 5c 3 x 10c 
Combination 1667: 24 x 1c 18 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1668: 24 x 1c 18 x 2c 4 x 10c 
Combination 1669: 24 x 1c 18 x 2c 2 x 10c 1 x 20c 
Combination 1670: 24 x 1c 18 x 2c 2 x 20c 
Combination 1671: 24 x 1c 13 x 2c 10 x 5c 
Combination 1672: 24 x 1c 13 x 2c 8 x 5c 1 x 10c 
Combination 1673: 24 x 1c 13 x 2c 6 x 5c 2 x 10c 
Combination 1674: 24 x 1c 13 x 2c 6 x 5c 1 x 20c 
Combination 1675: 24 x 1c 13 x 2c 4 x 5c 3 x 10c 
Combination 1676: 24 x 1c 13 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1677: 24 x 1c 13 x 2c 2 x 5c 4 x 10c 
Combination 1678: 24 x 1c 13 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1679: 24 x 1c 13 x 2c 2 x 5c 2 x 20c 
Combination 1680: 24 x 1c 13 x 2c 5 x 10c 
Combination 1681: 24 x 1c 13 x 2c 3 x 10c 1 x 20c 
Combination 1682: 24 x 1c 13 x 2c 1 x 10c 2 x 20c 
Combination 1683: 24 x 1c 13 x 2c 1 x 50c 
Combination 1684: 24 x 1c 8 x 2c 12 x 5c 
Combination 1685: 24 x 1c 8 x 2c 10 x 5c 1 x 10c 
Combination 1686: 24 x 1c 8 x 2c 8 x 5c 2 x 10c 
Combination 1687: 24 x 1c 8 x 2c 8 x 5c 1 x 20c 
Combination 1688: 24 x 1c 8 x 2c 6 x 5c 3 x 10c 
Combination 1689: 24 x 1c 8 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1690: 24 x 1c 8 x 2c 4 x 5c 4 x 10c 
Combination 1691: 24 x 1c 8 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1692: 24 x 1c 8 x 2c 4 x 5c 2 x 20c 
Combination 1693: 24 x 1c 8 x 2c 2 x 5c 5 x 10c 
Combination 1694: 24 x 1c 8 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1695: 24 x 1c 8 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1696: 24 x 1c 8 x 2c 2 x 5c 1 x 50c 
Combination 1697: 24 x 1c 8 x 2c 6 x 10c 
Combination 1698: 24 x 1c 8 x 2c 4 x 10c 1 x 20c 
Combination 1699: 24 x 1c 8 x 2c 2 x 10c 2 x 20c 
Combination 1700: 24 x 1c 8 x 2c 1 x 10c 1 x 50c 
Combination 1701: 24 x 1c 8 x 2c 3 x 20c 
Combination 1702: 24 x 1c 3 x 2c 14 x 5c 
Combination 1703: 24 x 1c 3 x 2c 12 x 5c 1 x 10c 
Combination 1704: 24 x 1c 3 x 2c 10 x 5c 2 x 10c 
Combination 1705: 24 x 1c 3 x 2c 10 x 5c 1 x 20c 
Combination 1706: 24 x 1c 3 x 2c 8 x 5c 3 x 10c 
Combination 1707: 24 x 1c 3 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 1708: 24 x 1c 3 x 2c 6 x 5c 4 x 10c 
Combination 1709: 24 x 1c 3 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 1710: 24 x 1c 3 x 2c 6 x 5c 2 x 20c 
Combination 1711: 24 x 1c 3 x 2c 4 x 5c 5 x 10c 
Combination 1712: 24 x 1c 3 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 1713: 24 x 1c 3 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 1714: 24 x 1c 3 x 2c 4 x 5c 1 x 50c 
Combination 1715: 24 x 1c 3 x 2c 2 x 5c 6 x 10c 
Combination 1716: 24 x 1c 3 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 1717: 24 x 1c 3 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 1718: 24 x 1c 3 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 1719: 24 x 1c 3 x 2c 2 x 5c 3 x 20c 
Combination 1720: 24 x 1c 3 x 2c 7 x 10c 
Combination 1721: 24 x 1c 3 x 2c 5 x 10c 1 x 20c 
Combination 1722: 24 x 1c 3 x 2c 3 x 10c 2 x 20c 
Combination 1723: 24 x 1c 3 x 2c 2 x 10c 1 x 50c 
Combination 1724: 24 x 1c 3 x 2c 1 x 10c 3 x 20c 
Combination 1725: 24 x 1c 3 x 2c 1 x 20c 1 x 50c 
Combination 1726: 23 x 1c 36 x 2c 1 x 5c 
Combination 1727: 23 x 1c 31 x 2c 3 x 5c 
Combination 1728: 23 x 1c 31 x 2c 1 x 5c 1 x 10c 
Combination 1729: 23 x 1c 26 x 2c 5 x 5c 
Combination 1730: 23 x 1c 26 x 2c 3 x 5c 1 x 10c 
Combination 1731: 23 x 1c 26 x 2c 1 x 5c 2 x 10c 
Combination 1732: 23 x 1c 26 x 2c 1 x 5c 1 x 20c 
Combination 1733: 23 x 1c 21 x 2c 7 x 5c 
Combination 1734: 23 x 1c 21 x 2c 5 x 5c 1 x 10c 
Combination 1735: 23 x 1c 21 x 2c 3 x 5c 2 x 10c 
Combination 1736: 23 x 1c 21 x 2c 3 x 5c 1 x 20c 
Combination 1737: 23 x 1c 21 x 2c 1 x 5c 3 x 10c 
Combination 1738: 23 x 1c 21 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1739: 23 x 1c 16 x 2c 9 x 5c 
Combination 1740: 23 x 1c 16 x 2c 7 x 5c 1 x 10c 
Combination 1741: 23 x 1c 16 x 2c 5 x 5c 2 x 10c 
Combination 1742: 23 x 1c 16 x 2c 5 x 5c 1 x 20c 
Combination 1743: 23 x 1c 16 x 2c 3 x 5c 3 x 10c 
Combination 1744: 23 x 1c 16 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1745: 23 x 1c 16 x 2c 1 x 5c 4 x 10c 
Combination 1746: 23 x 1c 16 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1747: 23 x 1c 16 x 2c 1 x 5c 2 x 20c 
Combination 1748: 23 x 1c 11 x 2c 11 x 5c 
Combination 1749: 23 x 1c 11 x 2c 9 x 5c 1 x 10c 
Combination 1750: 23 x 1c 11 x 2c 7 x 5c 2 x 10c 
Combination 1751: 23 x 1c 11 x 2c 7 x 5c 1 x 20c 
Combination 1752: 23 x 1c 11 x 2c 5 x 5c 3 x 10c 
Combination 1753: 23 x 1c 11 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1754: 23 x 1c 11 x 2c 3 x 5c 4 x 10c 
Combination 1755: 23 x 1c 11 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1756: 23 x 1c 11 x 2c 3 x 5c 2 x 20c 
Combination 1757: 23 x 1c 11 x 2c 1 x 5c 5 x 10c 
Combination 1758: 23 x 1c 11 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1759: 23 x 1c 11 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1760: 23 x 1c 11 x 2c 1 x 5c 1 x 50c 
Combination 1761: 23 x 1c 6 x 2c 13 x 5c 
Combination 1762: 23 x 1c 6 x 2c 11 x 5c 1 x 10c 
Combination 1763: 23 x 1c 6 x 2c 9 x 5c 2 x 10c 
Combination 1764: 23 x 1c 6 x 2c 9 x 5c 1 x 20c 
Combination 1765: 23 x 1c 6 x 2c 7 x 5c 3 x 10c 
Combination 1766: 23 x 1c 6 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1767: 23 x 1c 6 x 2c 5 x 5c 4 x 10c 
Combination 1768: 23 x 1c 6 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1769: 23 x 1c 6 x 2c 5 x 5c 2 x 20c 
Combination 1770: 23 x 1c 6 x 2c 3 x 5c 5 x 10c 
Combination 1771: 23 x 1c 6 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1772: 23 x 1c 6 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1773: 23 x 1c 6 x 2c 3 x 5c 1 x 50c 
Combination 1774: 23 x 1c 6 x 2c 1 x 5c 6 x 10c 
Combination 1775: 23 x 1c 6 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1776: 23 x 1c 6 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1777: 23 x 1c 6 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1778: 23 x 1c 6 x 2c 1 x 5c 3 x 20c 
Combination 1779: 23 x 1c 1 x 2c 15 x 5c 
Combination 1780: 23 x 1c 1 x 2c 13 x 5c 1 x 10c 
Combination 1781: 23 x 1c 1 x 2c 11 x 5c 2 x 10c 
Combination 1782: 23 x 1c 1 x 2c 11 x 5c 1 x 20c 
Combination 1783: 23 x 1c 1 x 2c 9 x 5c 3 x 10c 
Combination 1784: 23 x 1c 1 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 1785: 23 x 1c 1 x 2c 7 x 5c 4 x 10c 
Combination 1786: 23 x 1c 1 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 1787: 23 x 1c 1 x 2c 7 x 5c 2 x 20c 
Combination 1788: 23 x 1c 1 x 2c 5 x 5c 5 x 10c 
Combination 1789: 23 x 1c 1 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 1790: 23 x 1c 1 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 1791: 23 x 1c 1 x 2c 5 x 5c 1 x 50c 
Combination 1792: 23 x 1c 1 x 2c 3 x 5c 6 x 10c 
Combination 1793: 23 x 1c 1 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 1794: 23 x 1c 1 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 1795: 23 x 1c 1 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 1796: 23 x 1c 1 x 2c 3 x 5c 3 x 20c 
Combination 1797: 23 x 1c 1 x 2c 1 x 5c 7 x 10c 
Combination 1798: 23 x 1c 1 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 1799: 23 x 1c 1 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 1800: 23 x 1c 1 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 1801: 23 x 1c 1 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 1802: 23 x 1c 1 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 1803: 22 x 1c 39 x 2c 
Combination 1804: 22 x 1c 34 x 2c 2 x 5c 
Combination 1805: 22 x 1c 34 x 2c 1 x 10c 
Combination 1806: 22 x 1c 29 x 2c 4 x 5c 
Combination 1807: 22 x 1c 29 x 2c 2 x 5c 1 x 10c 
Combination 1808: 22 x 1c 29 x 2c 2 x 10c 
Combination 1809: 22 x 1c 29 x 2c 1 x 20c 
Combination 1810: 22 x 1c 24 x 2c 6 x 5c 
Combination 1811: 22 x 1c 24 x 2c 4 x 5c 1 x 10c 
Combination 1812: 22 x 1c 24 x 2c 2 x 5c 2 x 10c 
Combination 1813: 22 x 1c 24 x 2c 2 x 5c 1 x 20c 
Combination 1814: 22 x 1c 24 x 2c 3 x 10c 
Combination 1815: 22 x 1c 24 x 2c 1 x 10c 1 x 20c 
Combination 1816: 22 x 1c 19 x 2c 8 x 5c 
Combination 1817: 22 x 1c 19 x 2c 6 x 5c 1 x 10c 
Combination 1818: 22 x 1c 19 x 2c 4 x 5c 2 x 10c 
Combination 1819: 22 x 1c 19 x 2c 4 x 5c 1 x 20c 
Combination 1820: 22 x 1c 19 x 2c 2 x 5c 3 x 10c 
Combination 1821: 22 x 1c 19 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1822: 22 x 1c 19 x 2c 4 x 10c 
Combination 1823: 22 x 1c 19 x 2c 2 x 10c 1 x 20c 
Combination 1824: 22 x 1c 19 x 2c 2 x 20c 
Combination 1825: 22 x 1c 14 x 2c 10 x 5c 
Combination 1826: 22 x 1c 14 x 2c 8 x 5c 1 x 10c 
Combination 1827: 22 x 1c 14 x 2c 6 x 5c 2 x 10c 
Combination 1828: 22 x 1c 14 x 2c 6 x 5c 1 x 20c 
Combination 1829: 22 x 1c 14 x 2c 4 x 5c 3 x 10c 
Combination 1830: 22 x 1c 14 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1831: 22 x 1c 14 x 2c 2 x 5c 4 x 10c 
Combination 1832: 22 x 1c 14 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1833: 22 x 1c 14 x 2c 2 x 5c 2 x 20c 
Combination 1834: 22 x 1c 14 x 2c 5 x 10c 
Combination 1835: 22 x 1c 14 x 2c 3 x 10c 1 x 20c 
Combination 1836: 22 x 1c 14 x 2c 1 x 10c 2 x 20c 
Combination 1837: 22 x 1c 14 x 2c 1 x 50c 
Combination 1838: 22 x 1c 9 x 2c 12 x 5c 
Combination 1839: 22 x 1c 9 x 2c 10 x 5c 1 x 10c 
Combination 1840: 22 x 1c 9 x 2c 8 x 5c 2 x 10c 
Combination 1841: 22 x 1c 9 x 2c 8 x 5c 1 x 20c 
Combination 1842: 22 x 1c 9 x 2c 6 x 5c 3 x 10c 
Combination 1843: 22 x 1c 9 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1844: 22 x 1c 9 x 2c 4 x 5c 4 x 10c 
Combination 1845: 22 x 1c 9 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 1846: 22 x 1c 9 x 2c 4 x 5c 2 x 20c 
Combination 1847: 22 x 1c 9 x 2c 2 x 5c 5 x 10c 
Combination 1848: 22 x 1c 9 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 1849: 22 x 1c 9 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 1850: 22 x 1c 9 x 2c 2 x 5c 1 x 50c 
Combination 1851: 22 x 1c 9 x 2c 6 x 10c 
Combination 1852: 22 x 1c 9 x 2c 4 x 10c 1 x 20c 
Combination 1853: 22 x 1c 9 x 2c 2 x 10c 2 x 20c 
Combination 1854: 22 x 1c 9 x 2c 1 x 10c 1 x 50c 
Combination 1855: 22 x 1c 9 x 2c 3 x 20c 
Combination 1856: 22 x 1c 4 x 2c 14 x 5c 
Combination 1857: 22 x 1c 4 x 2c 12 x 5c 1 x 10c 
Combination 1858: 22 x 1c 4 x 2c 10 x 5c 2 x 10c 
Combination 1859: 22 x 1c 4 x 2c 10 x 5c 1 x 20c 
Combination 1860: 22 x 1c 4 x 2c 8 x 5c 3 x 10c 
Combination 1861: 22 x 1c 4 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 1862: 22 x 1c 4 x 2c 6 x 5c 4 x 10c 
Combination 1863: 22 x 1c 4 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 1864: 22 x 1c 4 x 2c 6 x 5c 2 x 20c 
Combination 1865: 22 x 1c 4 x 2c 4 x 5c 5 x 10c 
Combination 1866: 22 x 1c 4 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 1867: 22 x 1c 4 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 1868: 22 x 1c 4 x 2c 4 x 5c 1 x 50c 
Combination 1869: 22 x 1c 4 x 2c 2 x 5c 6 x 10c 
Combination 1870: 22 x 1c 4 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 1871: 22 x 1c 4 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 1872: 22 x 1c 4 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 1873: 22 x 1c 4 x 2c 2 x 5c 3 x 20c 
Combination 1874: 22 x 1c 4 x 2c 7 x 10c 
Combination 1875: 22 x 1c 4 x 2c 5 x 10c 1 x 20c 
Combination 1876: 22 x 1c 4 x 2c 3 x 10c 2 x 20c 
Combination 1877: 22 x 1c 4 x 2c 2 x 10c 1 x 50c 
Combination 1878: 22 x 1c 4 x 2c 1 x 10c 3 x 20c 
Combination 1879: 22 x 1c 4 x 2c 1 x 20c 1 x 50c 
Combination 1880: 21 x 1c 37 x 2c 1 x 5c 
Combination 1881: 21 x 1c 32 x 2c 3 x 5c 
Combination 1882: 21 x 1c 32 x 2c 1 x 5c 1 x 10c 
Combination 1883: 21 x 1c 27 x 2c 5 x 5c 
Combination 1884: 21 x 1c 27 x 2c 3 x 5c 1 x 10c 
Combination 1885: 21 x 1c 27 x 2c 1 x 5c 2 x 10c 
Combination 1886: 21 x 1c 27 x 2c 1 x 5c 1 x 20c 
Combination 1887: 21 x 1c 22 x 2c 7 x 5c 
Combination 1888: 21 x 1c 22 x 2c 5 x 5c 1 x 10c 
Combination 1889: 21 x 1c 22 x 2c 3 x 5c 2 x 10c 
Combination 1890: 21 x 1c 22 x 2c 3 x 5c 1 x 20c 
Combination 1891: 21 x 1c 22 x 2c 1 x 5c 3 x 10c 
Combination 1892: 21 x 1c 22 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 1893: 21 x 1c 17 x 2c 9 x 5c 
Combination 1894: 21 x 1c 17 x 2c 7 x 5c 1 x 10c 
Combination 1895: 21 x 1c 17 x 2c 5 x 5c 2 x 10c 
Combination 1896: 21 x 1c 17 x 2c 5 x 5c 1 x 20c 
Combination 1897: 21 x 1c 17 x 2c 3 x 5c 3 x 10c 
Combination 1898: 21 x 1c 17 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 1899: 21 x 1c 17 x 2c 1 x 5c 4 x 10c 
Combination 1900: 21 x 1c 17 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 1901: 21 x 1c 17 x 2c 1 x 5c 2 x 20c 
Combination 1902: 21 x 1c 12 x 2c 11 x 5c 
Combination 1903: 21 x 1c 12 x 2c 9 x 5c 1 x 10c 
Combination 1904: 21 x 1c 12 x 2c 7 x 5c 2 x 10c 
Combination 1905: 21 x 1c 12 x 2c 7 x 5c 1 x 20c 
Combination 1906: 21 x 1c 12 x 2c 5 x 5c 3 x 10c 
Combination 1907: 21 x 1c 12 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 1908: 21 x 1c 12 x 2c 3 x 5c 4 x 10c 
Combination 1909: 21 x 1c 12 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 1910: 21 x 1c 12 x 2c 3 x 5c 2 x 20c 
Combination 1911: 21 x 1c 12 x 2c 1 x 5c 5 x 10c 
Combination 1912: 21 x 1c 12 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 1913: 21 x 1c 12 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 1914: 21 x 1c 12 x 2c 1 x 5c 1 x 50c 
Combination 1915: 21 x 1c 7 x 2c 13 x 5c 
Combination 1916: 21 x 1c 7 x 2c 11 x 5c 1 x 10c 
Combination 1917: 21 x 1c 7 x 2c 9 x 5c 2 x 10c 
Combination 1918: 21 x 1c 7 x 2c 9 x 5c 1 x 20c 
Combination 1919: 21 x 1c 7 x 2c 7 x 5c 3 x 10c 
Combination 1920: 21 x 1c 7 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 1921: 21 x 1c 7 x 2c 5 x 5c 4 x 10c 
Combination 1922: 21 x 1c 7 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 1923: 21 x 1c 7 x 2c 5 x 5c 2 x 20c 
Combination 1924: 21 x 1c 7 x 2c 3 x 5c 5 x 10c 
Combination 1925: 21 x 1c 7 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 1926: 21 x 1c 7 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 1927: 21 x 1c 7 x 2c 3 x 5c 1 x 50c 
Combination 1928: 21 x 1c 7 x 2c 1 x 5c 6 x 10c 
Combination 1929: 21 x 1c 7 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 1930: 21 x 1c 7 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 1931: 21 x 1c 7 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 1932: 21 x 1c 7 x 2c 1 x 5c 3 x 20c 
Combination 1933: 21 x 1c 2 x 2c 15 x 5c 
Combination 1934: 21 x 1c 2 x 2c 13 x 5c 1 x 10c 
Combination 1935: 21 x 1c 2 x 2c 11 x 5c 2 x 10c 
Combination 1936: 21 x 1c 2 x 2c 11 x 5c 1 x 20c 
Combination 1937: 21 x 1c 2 x 2c 9 x 5c 3 x 10c 
Combination 1938: 21 x 1c 2 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 1939: 21 x 1c 2 x 2c 7 x 5c 4 x 10c 
Combination 1940: 21 x 1c 2 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 1941: 21 x 1c 2 x 2c 7 x 5c 2 x 20c 
Combination 1942: 21 x 1c 2 x 2c 5 x 5c 5 x 10c 
Combination 1943: 21 x 1c 2 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 1944: 21 x 1c 2 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 1945: 21 x 1c 2 x 2c 5 x 5c 1 x 50c 
Combination 1946: 21 x 1c 2 x 2c 3 x 5c 6 x 10c 
Combination 1947: 21 x 1c 2 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 1948: 21 x 1c 2 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 1949: 21 x 1c 2 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 1950: 21 x 1c 2 x 2c 3 x 5c 3 x 20c 
Combination 1951: 21 x 1c 2 x 2c 1 x 5c 7 x 10c 
Combination 1952: 21 x 1c 2 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 1953: 21 x 1c 2 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 1954: 21 x 1c 2 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 1955: 21 x 1c 2 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 1956: 21 x 1c 2 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 1957: 20 x 1c 40 x 2c 
Combination 1958: 20 x 1c 35 x 2c 2 x 5c 
Combination 1959: 20 x 1c 35 x 2c 1 x 10c 
Combination 1960: 20 x 1c 30 x 2c 4 x 5c 
Combination 1961: 20 x 1c 30 x 2c 2 x 5c 1 x 10c 
Combination 1962: 20 x 1c 30 x 2c 2 x 10c 
Combination 1963: 20 x 1c 30 x 2c 1 x 20c 
Combination 1964: 20 x 1c 25 x 2c 6 x 5c 
Combination 1965: 20 x 1c 25 x 2c 4 x 5c 1 x 10c 
Combination 1966: 20 x 1c 25 x 2c 2 x 5c 2 x 10c 
Combination 1967: 20 x 1c 25 x 2c 2 x 5c 1 x 20c 
Combination 1968: 20 x 1c 25 x 2c 3 x 10c 
Combination 1969: 20 x 1c 25 x 2c 1 x 10c 1 x 20c 
Combination 1970: 20 x 1c 20 x 2c 8 x 5c 
Combination 1971: 20 x 1c 20 x 2c 6 x 5c 1 x 10c 
Combination 1972: 20 x 1c 20 x 2c 4 x 5c 2 x 10c 
Combination 1973: 20 x 1c 20 x 2c 4 x 5c 1 x 20c 
Combination 1974: 20 x 1c 20 x 2c 2 x 5c 3 x 10c 
Combination 1975: 20 x 1c 20 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 1976: 20 x 1c 20 x 2c 4 x 10c 
Combination 1977: 20 x 1c 20 x 2c 2 x 10c 1 x 20c 
Combination 1978: 20 x 1c 20 x 2c 2 x 20c 
Combination 1979: 20 x 1c 15 x 2c 10 x 5c 
Combination 1980: 20 x 1c 15 x 2c 8 x 5c 1 x 10c 
Combination 1981: 20 x 1c 15 x 2c 6 x 5c 2 x 10c 
Combination 1982: 20 x 1c 15 x 2c 6 x 5c 1 x 20c 
Combination 1983: 20 x 1c 15 x 2c 4 x 5c 3 x 10c 
Combination 1984: 20 x 1c 15 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 1985: 20 x 1c 15 x 2c 2 x 5c 4 x 10c 
Combination 1986: 20 x 1c 15 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 1987: 20 x 1c 15 x 2c 2 x 5c 2 x 20c 
Combination 1988: 20 x 1c 15 x 2c 5 x 10c 
Combination 1989: 20 x 1c 15 x 2c 3 x 10c 1 x 20c 
Combination 1990: 20 x 1c 15 x 2c 1 x 10c 2 x 20c 
Combination 1991: 20 x 1c 15 x 2c 1 x 50c 
Combination 1992: 20 x 1c 10 x 2c 12 x 5c 
Combination 1993: 20 x 1c 10 x 2c 10 x 5c 1 x 10c 
Combination 1994: 20 x 1c 10 x 2c 8 x 5c 2 x 10c 
Combination 1995: 20 x 1c 10 x 2c 8 x 5c 1 x 20c 
Combination 1996: 20 x 1c 10 x 2c 6 x 5c 3 x 10c 
Combination 1997: 20 x 1c 10 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 1998: 20 x 1c 10 x 2c 4 x 5c 4 x 10c 
Combination 1999: 20 x 1c 10 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 2000: 20 x 1c 10 x 2c 4 x 5c 2 x 20c 
Combination 2001: 20 x 1c 10 x 2c 2 x 5c 5 x 10c 
Combination 2002: 20 x 1c 10 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 2003: 20 x 1c 10 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 2004: 20 x 1c 10 x 2c 2 x 5c 1 x 50c 
Combination 2005: 20 x 1c 10 x 2c 6 x 10c 
Combination 2006: 20 x 1c 10 x 2c 4 x 10c 1 x 20c 
Combination 2007: 20 x 1c 10 x 2c 2 x 10c 2 x 20c 
Combination 2008: 20 x 1c 10 x 2c 1 x 10c 1 x 50c 
Combination 2009: 20 x 1c 10 x 2c 3 x 20c 
Combination 2010: 20 x 1c 5 x 2c 14 x 5c 
Combination 2011: 20 x 1c 5 x 2c 12 x 5c 1 x 10c 
Combination 2012: 20 x 1c 5 x 2c 10 x 5c 2 x 10c 
Combination 2013: 20 x 1c 5 x 2c 10 x 5c 1 x 20c 
Combination 2014: 20 x 1c 5 x 2c 8 x 5c 3 x 10c 
Combination 2015: 20 x 1c 5 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 2016: 20 x 1c 5 x 2c 6 x 5c 4 x 10c 
Combination 2017: 20 x 1c 5 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 2018: 20 x 1c 5 x 2c 6 x 5c 2 x 20c 
Combination 2019: 20 x 1c 5 x 2c 4 x 5c 5 x 10c 
Combination 2020: 20 x 1c 5 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 2021: 20 x 1c 5 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 2022: 20 x 1c 5 x 2c 4 x 5c 1 x 50c 
Combination 2023: 20 x 1c 5 x 2c 2 x 5c 6 x 10c 
Combination 2024: 20 x 1c 5 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 2025: 20 x 1c 5 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 2026: 20 x 1c 5 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 2027: 20 x 1c 5 x 2c 2 x 5c 3 x 20c 
Combination 2028: 20 x 1c 5 x 2c 7 x 10c 
Combination 2029: 20 x 1c 5 x 2c 5 x 10c 1 x 20c 
Combination 2030: 20 x 1c 5 x 2c 3 x 10c 2 x 20c 
Combination 2031: 20 x 1c 5 x 2c 2 x 10c 1 x 50c 
Combination 2032: 20 x 1c 5 x 2c 1 x 10c 3 x 20c 
Combination 2033: 20 x 1c 5 x 2c 1 x 20c 1 x 50c 
Combination 2034: 20 x 1c 16 x 5c 
Combination 2035: 20 x 1c 14 x 5c 1 x 10c 
Combination 2036: 20 x 1c 12 x 5c 2 x 10c 
Combination 2037: 20 x 1c 12 x 5c 1 x 20c 
Combination 2038: 20 x 1c 10 x 5c 3 x 10c 
Combination 2039: 20 x 1c 10 x 5c 1 x 10c 1 x 20c 
Combination 2040: 20 x 1c 8 x 5c 4 x 10c 
Combination 2041: 20 x 1c 8 x 5c 2 x 10c 1 x 20c 
Combination 2042: 20 x 1c 8 x 5c 2 x 20c 
Combination 2043: 20 x 1c 6 x 5c 5 x 10c 
Combination 2044: 20 x 1c 6 x 5c 3 x 10c 1 x 20c 
Combination 2045: 20 x 1c 6 x 5c 1 x 10c 2 x 20c 
Combination 2046: 20 x 1c 6 x 5c 1 x 50c 
Combination 2047: 20 x 1c 4 x 5c 6 x 10c 
Combination 2048: 20 x 1c 4 x 5c 4 x 10c 1 x 20c 
Combination 2049: 20 x 1c 4 x 5c 2 x 10c 2 x 20c 
Combination 2050: 20 x 1c 4 x 5c 1 x 10c 1 x 50c 
Combination 2051: 20 x 1c 4 x 5c 3 x 20c 
Combination 2052: 20 x 1c 2 x 5c 7 x 10c 
Combination 2053: 20 x 1c 2 x 5c 5 x 10c 1 x 20c 
Combination 2054: 20 x 1c 2 x 5c 3 x 10c 2 x 20c 
Combination 2055: 20 x 1c 2 x 5c 2 x 10c 1 x 50c 
Combination 2056: 20 x 1c 2 x 5c 1 x 10c 3 x 20c 
Combination 2057: 20 x 1c 2 x 5c 1 x 20c 1 x 50c 
Combination 2058: 20 x 1c 8 x 10c 
Combination 2059: 20 x 1c 6 x 10c 1 x 20c 
Combination 2060: 20 x 1c 4 x 10c 2 x 20c 
Combination 2061: 20 x 1c 3 x 10c 1 x 50c 
Combination 2062: 20 x 1c 2 x 10c 3 x 20c 
Combination 2063: 20 x 1c 1 x 10c 1 x 20c 1 x 50c 
Combination 2064: 20 x 1c 4 x 20c 
Combination 2065: 19 x 1c 38 x 2c 1 x 5c 
Combination 2066: 19 x 1c 33 x 2c 3 x 5c 
Combination 2067: 19 x 1c 33 x 2c 1 x 5c 1 x 10c 
Combination 2068: 19 x 1c 28 x 2c 5 x 5c 
Combination 2069: 19 x 1c 28 x 2c 3 x 5c 1 x 10c 
Combination 2070: 19 x 1c 28 x 2c 1 x 5c 2 x 10c 
Combination 2071: 19 x 1c 28 x 2c 1 x 5c 1 x 20c 
Combination 2072: 19 x 1c 23 x 2c 7 x 5c 
Combination 2073: 19 x 1c 23 x 2c 5 x 5c 1 x 10c 
Combination 2074: 19 x 1c 23 x 2c 3 x 5c 2 x 10c 
Combination 2075: 19 x 1c 23 x 2c 3 x 5c 1 x 20c 
Combination 2076: 19 x 1c 23 x 2c 1 x 5c 3 x 10c 
Combination 2077: 19 x 1c 23 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 2078: 19 x 1c 18 x 2c 9 x 5c 
Combination 2079: 19 x 1c 18 x 2c 7 x 5c 1 x 10c 
Combination 2080: 19 x 1c 18 x 2c 5 x 5c 2 x 10c 
Combination 2081: 19 x 1c 18 x 2c 5 x 5c 1 x 20c 
Combination 2082: 19 x 1c 18 x 2c 3 x 5c 3 x 10c 
Combination 2083: 19 x 1c 18 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 2084: 19 x 1c 18 x 2c 1 x 5c 4 x 10c 
Combination 2085: 19 x 1c 18 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 2086: 19 x 1c 18 x 2c 1 x 5c 2 x 20c 
Combination 2087: 19 x 1c 13 x 2c 11 x 5c 
Combination 2088: 19 x 1c 13 x 2c 9 x 5c 1 x 10c 
Combination 2089: 19 x 1c 13 x 2c 7 x 5c 2 x 10c 
Combination 2090: 19 x 1c 13 x 2c 7 x 5c 1 x 20c 
Combination 2091: 19 x 1c 13 x 2c 5 x 5c 3 x 10c 
Combination 2092: 19 x 1c 13 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 2093: 19 x 1c 13 x 2c 3 x 5c 4 x 10c 
Combination 2094: 19 x 1c 13 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 2095: 19 x 1c 13 x 2c 3 x 5c 2 x 20c 
Combination 2096: 19 x 1c 13 x 2c 1 x 5c 5 x 10c 
Combination 2097: 19 x 1c 13 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 2098: 19 x 1c 13 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 2099: 19 x 1c 13 x 2c 1 x 5c 1 x 50c 
Combination 2100: 19 x 1c 8 x 2c 13 x 5c 
Combination 2101: 19 x 1c 8 x 2c 11 x 5c 1 x 10c 
Combination 2102: 19 x 1c 8 x 2c 9 x 5c 2 x 10c 
Combination 2103: 19 x 1c 8 x 2c 9 x 5c 1 x 20c 
Combination 2104: 19 x 1c 8 x 2c 7 x 5c 3 x 10c 
Combination 2105: 19 x 1c 8 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 2106: 19 x 1c 8 x 2c 5 x 5c 4 x 10c 
Combination 2107: 19 x 1c 8 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 2108: 19 x 1c 8 x 2c 5 x 5c 2 x 20c 
Combination 2109: 19 x 1c 8 x 2c 3 x 5c 5 x 10c 
Combination 2110: 19 x 1c 8 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 2111: 19 x 1c 8 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 2112: 19 x 1c 8 x 2c 3 x 5c 1 x 50c 
Combination 2113: 19 x 1c 8 x 2c 1 x 5c 6 x 10c 
Combination 2114: 19 x 1c 8 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 2115: 19 x 1c 8 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 2116: 19 x 1c 8 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 2117: 19 x 1c 8 x 2c 1 x 5c 3 x 20c 
Combination 2118: 19 x 1c 3 x 2c 15 x 5c 
Combination 2119: 19 x 1c 3 x 2c 13 x 5c 1 x 10c 
Combination 2120: 19 x 1c 3 x 2c 11 x 5c 2 x 10c 
Combination 2121: 19 x 1c 3 x 2c 11 x 5c 1 x 20c 
Combination 2122: 19 x 1c 3 x 2c 9 x 5c 3 x 10c 
Combination 2123: 19 x 1c 3 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 2124: 19 x 1c 3 x 2c 7 x 5c 4 x 10c 
Combination 2125: 19 x 1c 3 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 2126: 19 x 1c 3 x 2c 7 x 5c 2 x 20c 
Combination 2127: 19 x 1c 3 x 2c 5 x 5c 5 x 10c 
Combination 2128: 19 x 1c 3 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 2129: 19 x 1c 3 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 2130: 19 x 1c 3 x 2c 5 x 5c 1 x 50c 
Combination 2131: 19 x 1c 3 x 2c 3 x 5c 6 x 10c 
Combination 2132: 19 x 1c 3 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 2133: 19 x 1c 3 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 2134: 19 x 1c 3 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 2135: 19 x 1c 3 x 2c 3 x 5c 3 x 20c 
Combination 2136: 19 x 1c 3 x 2c 1 x 5c 7 x 10c 
Combination 2137: 19 x 1c 3 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 2138: 19 x 1c 3 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 2139: 19 x 1c 3 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 2140: 19 x 1c 3 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 2141: 19 x 1c 3 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 2142: 18 x 1c 41 x 2c 
Combination 2143: 18 x 1c 36 x 2c 2 x 5c 
Combination 2144: 18 x 1c 36 x 2c 1 x 10c 
Combination 2145: 18 x 1c 31 x 2c 4 x 5c 
Combination 2146: 18 x 1c 31 x 2c 2 x 5c 1 x 10c 
Combination 2147: 18 x 1c 31 x 2c 2 x 10c 
Combination 2148: 18 x 1c 31 x 2c 1 x 20c 
Combination 2149: 18 x 1c 26 x 2c 6 x 5c 
Combination 2150: 18 x 1c 26 x 2c 4 x 5c 1 x 10c 
Combination 2151: 18 x 1c 26 x 2c 2 x 5c 2 x 10c 
Combination 2152: 18 x 1c 26 x 2c 2 x 5c 1 x 20c 
Combination 2153: 18 x 1c 26 x 2c 3 x 10c 
Combination 2154: 18 x 1c 26 x 2c 1 x 10c 1 x 20c 
Combination 2155: 18 x 1c 21 x 2c 8 x 5c 
Combination 2156: 18 x 1c 21 x 2c 6 x 5c 1 x 10c 
Combination 2157: 18 x 1c 21 x 2c 4 x 5c 2 x 10c 
Combination 2158: 18 x 1c 21 x 2c 4 x 5c 1 x 20c 
Combination 2159: 18 x 1c 21 x 2c 2 x 5c 3 x 10c 
Combination 2160: 18 x 1c 21 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 2161: 18 x 1c 21 x 2c 4 x 10c 
Combination 2162: 18 x 1c 21 x 2c 2 x 10c 1 x 20c 
Combination 2163: 18 x 1c 21 x 2c 2 x 20c 
Combination 2164: 18 x 1c 16 x 2c 10 x 5c 
Combination 2165: 18 x 1c 16 x 2c 8 x 5c 1 x 10c 
Combination 2166: 18 x 1c 16 x 2c 6 x 5c 2 x 10c 
Combination 2167: 18 x 1c 16 x 2c 6 x 5c 1 x 20c 
Combination 2168: 18 x 1c 16 x 2c 4 x 5c 3 x 10c 
Combination 2169: 18 x 1c 16 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 2170: 18 x 1c 16 x 2c 2 x 5c 4 x 10c 
Combination 2171: 18 x 1c 16 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 2172: 18 x 1c 16 x 2c 2 x 5c 2 x 20c 
Combination 2173: 18 x 1c 16 x 2c 5 x 10c 
Combination 2174: 18 x 1c 16 x 2c 3 x 10c 1 x 20c 
Combination 2175: 18 x 1c 16 x 2c 1 x 10c 2 x 20c 
Combination 2176: 18 x 1c 16 x 2c 1 x 50c 
Combination 2177: 18 x 1c 11 x 2c 12 x 5c 
Combination 2178: 18 x 1c 11 x 2c 10 x 5c 1 x 10c 
Combination 2179: 18 x 1c 11 x 2c 8 x 5c 2 x 10c 
Combination 2180: 18 x 1c 11 x 2c 8 x 5c 1 x 20c 
Combination 2181: 18 x 1c 11 x 2c 6 x 5c 3 x 10c 
Combination 2182: 18 x 1c 11 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 2183: 18 x 1c 11 x 2c 4 x 5c 4 x 10c 
Combination 2184: 18 x 1c 11 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 2185: 18 x 1c 11 x 2c 4 x 5c 2 x 20c 
Combination 2186: 18 x 1c 11 x 2c 2 x 5c 5 x 10c 
Combination 2187: 18 x 1c 11 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 2188: 18 x 1c 11 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 2189: 18 x 1c 11 x 2c 2 x 5c 1 x 50c 
Combination 2190: 18 x 1c 11 x 2c 6 x 10c 
Combination 2191: 18 x 1c 11 x 2c 4 x 10c 1 x 20c 
Combination 2192: 18 x 1c 11 x 2c 2 x 10c 2 x 20c 
Combination 2193: 18 x 1c 11 x 2c 1 x 10c 1 x 50c 
Combination 2194: 18 x 1c 11 x 2c 3 x 20c 
Combination 2195: 18 x 1c 6 x 2c 14 x 5c 
Combination 2196: 18 x 1c 6 x 2c 12 x 5c 1 x 10c 
Combination 2197: 18 x 1c 6 x 2c 10 x 5c 2 x 10c 
Combination 2198: 18 x 1c 6 x 2c 10 x 5c 1 x 20c 
Combination 2199: 18 x 1c 6 x 2c 8 x 5c 3 x 10c 
Combination 2200: 18 x 1c 6 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 2201: 18 x 1c 6 x 2c 6 x 5c 4 x 10c 
Combination 2202: 18 x 1c 6 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 2203: 18 x 1c 6 x 2c 6 x 5c 2 x 20c 
Combination 2204: 18 x 1c 6 x 2c 4 x 5c 5 x 10c 
Combination 2205: 18 x 1c 6 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 2206: 18 x 1c 6 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 2207: 18 x 1c 6 x 2c 4 x 5c 1 x 50c 
Combination 2208: 18 x 1c 6 x 2c 2 x 5c 6 x 10c 
Combination 2209: 18 x 1c 6 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 2210: 18 x 1c 6 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 2211: 18 x 1c 6 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 2212: 18 x 1c 6 x 2c 2 x 5c 3 x 20c 
Combination 2213: 18 x 1c 6 x 2c 7 x 10c 
Combination 2214: 18 x 1c 6 x 2c 5 x 10c 1 x 20c 
Combination 2215: 18 x 1c 6 x 2c 3 x 10c 2 x 20c 
Combination 2216: 18 x 1c 6 x 2c 2 x 10c 1 x 50c 
Combination 2217: 18 x 1c 6 x 2c 1 x 10c 3 x 20c 
Combination 2218: 18 x 1c 6 x 2c 1 x 20c 1 x 50c 
Combination 2219: 18 x 1c 1 x 2c 16 x 5c 
Combination 2220: 18 x 1c 1 x 2c 14 x 5c 1 x 10c 
Combination 2221: 18 x 1c 1 x 2c 12 x 5c 2 x 10c 
Combination 2222: 18 x 1c 1 x 2c 12 x 5c 1 x 20c 
Combination 2223: 18 x 1c 1 x 2c 10 x 5c 3 x 10c 
Combination 2224: 18 x 1c 1 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 2225: 18 x 1c 1 x 2c 8 x 5c 4 x 10c 
Combination 2226: 18 x 1c 1 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 2227: 18 x 1c 1 x 2c 8 x 5c 2 x 20c 
Combination 2228: 18 x 1c 1 x 2c 6 x 5c 5 x 10c 
Combination 2229: 18 x 1c 1 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 2230: 18 x 1c 1 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 2231: 18 x 1c 1 x 2c 6 x 5c 1 x 50c 
Combination 2232: 18 x 1c 1 x 2c 4 x 5c 6 x 10c 
Combination 2233: 18 x 1c 1 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 2234: 18 x 1c 1 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 2235: 18 x 1c 1 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 2236: 18 x 1c 1 x 2c 4 x 5c 3 x 20c 
Combination 2237: 18 x 1c 1 x 2c 2 x 5c 7 x 10c 
Combination 2238: 18 x 1c 1 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 2239: 18 x 1c 1 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 2240: 18 x 1c 1 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 2241: 18 x 1c 1 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 2242: 18 x 1c 1 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 2243: 18 x 1c 1 x 2c 8 x 10c 
Combination 2244: 18 x 1c 1 x 2c 6 x 10c 1 x 20c 
Combination 2245: 18 x 1c 1 x 2c 4 x 10c 2 x 20c 
Combination 2246: 18 x 1c 1 x 2c 3 x 10c 1 x 50c 
Combination 2247: 18 x 1c 1 x 2c 2 x 10c 3 x 20c 
Combination 2248: 18 x 1c 1 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 2249: 18 x 1c 1 x 2c 4 x 20c 
Combination 2250: 17 x 1c 39 x 2c 1 x 5c 
Combination 2251: 17 x 1c 34 x 2c 3 x 5c 
Combination 2252: 17 x 1c 34 x 2c 1 x 5c 1 x 10c 
Combination 2253: 17 x 1c 29 x 2c 5 x 5c 
Combination 2254: 17 x 1c 29 x 2c 3 x 5c 1 x 10c 
Combination 2255: 17 x 1c 29 x 2c 1 x 5c 2 x 10c 
Combination 2256: 17 x 1c 29 x 2c 1 x 5c 1 x 20c 
Combination 2257: 17 x 1c 24 x 2c 7 x 5c 
Combination 2258: 17 x 1c 24 x 2c 5 x 5c 1 x 10c 
Combination 2259: 17 x 1c 24 x 2c 3 x 5c 2 x 10c 
Combination 2260: 17 x 1c 24 x 2c 3 x 5c 1 x 20c 
Combination 2261: 17 x 1c 24 x 2c 1 x 5c 3 x 10c 
Combination 2262: 17 x 1c 24 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 2263: 17 x 1c 19 x 2c 9 x 5c 
Combination 2264: 17 x 1c 19 x 2c 7 x 5c 1 x 10c 
Combination 2265: 17 x 1c 19 x 2c 5 x 5c 2 x 10c 
Combination 2266: 17 x 1c 19 x 2c 5 x 5c 1 x 20c 
Combination 2267: 17 x 1c 19 x 2c 3 x 5c 3 x 10c 
Combination 2268: 17 x 1c 19 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 2269: 17 x 1c 19 x 2c 1 x 5c 4 x 10c 
Combination 2270: 17 x 1c 19 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 2271: 17 x 1c 19 x 2c 1 x 5c 2 x 20c 
Combination 2272: 17 x 1c 14 x 2c 11 x 5c 
Combination 2273: 17 x 1c 14 x 2c 9 x 5c 1 x 10c 
Combination 2274: 17 x 1c 14 x 2c 7 x 5c 2 x 10c 
Combination 2275: 17 x 1c 14 x 2c 7 x 5c 1 x 20c 
Combination 2276: 17 x 1c 14 x 2c 5 x 5c 3 x 10c 
Combination 2277: 17 x 1c 14 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 2278: 17 x 1c 14 x 2c 3 x 5c 4 x 10c 
Combination 2279: 17 x 1c 14 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 2280: 17 x 1c 14 x 2c 3 x 5c 2 x 20c 
Combination 2281: 17 x 1c 14 x 2c 1 x 5c 5 x 10c 
Combination 2282: 17 x 1c 14 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 2283: 17 x 1c 14 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 2284: 17 x 1c 14 x 2c 1 x 5c 1 x 50c 
Combination 2285: 17 x 1c 9 x 2c 13 x 5c 
Combination 2286: 17 x 1c 9 x 2c 11 x 5c 1 x 10c 
Combination 2287: 17 x 1c 9 x 2c 9 x 5c 2 x 10c 
Combination 2288: 17 x 1c 9 x 2c 9 x 5c 1 x 20c 
Combination 2289: 17 x 1c 9 x 2c 7 x 5c 3 x 10c 
Combination 2290: 17 x 1c 9 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 2291: 17 x 1c 9 x 2c 5 x 5c 4 x 10c 
Combination 2292: 17 x 1c 9 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 2293: 17 x 1c 9 x 2c 5 x 5c 2 x 20c 
Combination 2294: 17 x 1c 9 x 2c 3 x 5c 5 x 10c 
Combination 2295: 17 x 1c 9 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 2296: 17 x 1c 9 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 2297: 17 x 1c 9 x 2c 3 x 5c 1 x 50c 
Combination 2298: 17 x 1c 9 x 2c 1 x 5c 6 x 10c 
Combination 2299: 17 x 1c 9 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 2300: 17 x 1c 9 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 2301: 17 x 1c 9 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 2302: 17 x 1c 9 x 2c 1 x 5c 3 x 20c 
Combination 2303: 17 x 1c 4 x 2c 15 x 5c 
Combination 2304: 17 x 1c 4 x 2c 13 x 5c 1 x 10c 
Combination 2305: 17 x 1c 4 x 2c 11 x 5c 2 x 10c 
Combination 2306: 17 x 1c 4 x 2c 11 x 5c 1 x 20c 
Combination 2307: 17 x 1c 4 x 2c 9 x 5c 3 x 10c 
Combination 2308: 17 x 1c 4 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 2309: 17 x 1c 4 x 2c 7 x 5c 4 x 10c 
Combination 2310: 17 x 1c 4 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 2311: 17 x 1c 4 x 2c 7 x 5c 2 x 20c 
Combination 2312: 17 x 1c 4 x 2c 5 x 5c 5 x 10c 
Combination 2313: 17 x 1c 4 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 2314: 17 x 1c 4 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 2315: 17 x 1c 4 x 2c 5 x 5c 1 x 50c 
Combination 2316: 17 x 1c 4 x 2c 3 x 5c 6 x 10c 
Combination 2317: 17 x 1c 4 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 2318: 17 x 1c 4 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 2319: 17 x 1c 4 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 2320: 17 x 1c 4 x 2c 3 x 5c 3 x 20c 
Combination 2321: 17 x 1c 4 x 2c 1 x 5c 7 x 10c 
Combination 2322: 17 x 1c 4 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 2323: 17 x 1c 4 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 2324: 17 x 1c 4 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 2325: 17 x 1c 4 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 2326: 17 x 1c 4 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 2327: 16 x 1c 42 x 2c 
Combination 2328: 16 x 1c 37 x 2c 2 x 5c 
Combination 2329: 16 x 1c 37 x 2c 1 x 10c 
Combination 2330: 16 x 1c 32 x 2c 4 x 5c 
Combination 2331: 16 x 1c 32 x 2c 2 x 5c 1 x 10c 
Combination 2332: 16 x 1c 32 x 2c 2 x 10c 
Combination 2333: 16 x 1c 32 x 2c 1 x 20c 
Combination 2334: 16 x 1c 27 x 2c 6 x 5c 
Combination 2335: 16 x 1c 27 x 2c 4 x 5c 1 x 10c 
Combination 2336: 16 x 1c 27 x 2c 2 x 5c 2 x 10c 
Combination 2337: 16 x 1c 27 x 2c 2 x 5c 1 x 20c 
Combination 2338: 16 x 1c 27 x 2c 3 x 10c 
Combination 2339: 16 x 1c 27 x 2c 1 x 10c 1 x 20c 
Combination 2340: 16 x 1c 22 x 2c 8 x 5c 
Combination 2341: 16 x 1c 22 x 2c 6 x 5c 1 x 10c 
Combination 2342: 16 x 1c 22 x 2c 4 x 5c 2 x 10c 
Combination 2343: 16 x 1c 22 x 2c 4 x 5c 1 x 20c 
Combination 2344: 16 x 1c 22 x 2c 2 x 5c 3 x 10c 
Combination 2345: 16 x 1c 22 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 2346: 16 x 1c 22 x 2c 4 x 10c 
Combination 2347: 16 x 1c 22 x 2c 2 x 10c 1 x 20c 
Combination 2348: 16 x 1c 22 x 2c 2 x 20c 
Combination 2349: 16 x 1c 17 x 2c 10 x 5c 
Combination 2350: 16 x 1c 17 x 2c 8 x 5c 1 x 10c 
Combination 2351: 16 x 1c 17 x 2c 6 x 5c 2 x 10c 
Combination 2352: 16 x 1c 17 x 2c 6 x 5c 1 x 20c 
Combination 2353: 16 x 1c 17 x 2c 4 x 5c 3 x 10c 
Combination 2354: 16 x 1c 17 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 2355: 16 x 1c 17 x 2c 2 x 5c 4 x 10c 
Combination 2356: 16 x 1c 17 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 2357: 16 x 1c 17 x 2c 2 x 5c 2 x 20c 
Combination 2358: 16 x 1c 17 x 2c 5 x 10c 
Combination 2359: 16 x 1c 17 x 2c 3 x 10c 1 x 20c 
Combination 2360: 16 x 1c 17 x 2c 1 x 10c 2 x 20c 
Combination 2361: 16 x 1c 17 x 2c 1 x 50c 
Combination 2362: 16 x 1c 12 x 2c 12 x 5c 
Combination 2363: 16 x 1c 12 x 2c 10 x 5c 1 x 10c 
Combination 2364: 16 x 1c 12 x 2c 8 x 5c 2 x 10c 
Combination 2365: 16 x 1c 12 x 2c 8 x 5c 1 x 20c 
Combination 2366: 16 x 1c 12 x 2c 6 x 5c 3 x 10c 
Combination 2367: 16 x 1c 12 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 2368: 16 x 1c 12 x 2c 4 x 5c 4 x 10c 
Combination 2369: 16 x 1c 12 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 2370: 16 x 1c 12 x 2c 4 x 5c 2 x 20c 
Combination 2371: 16 x 1c 12 x 2c 2 x 5c 5 x 10c 
Combination 2372: 16 x 1c 12 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 2373: 16 x 1c 12 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 2374: 16 x 1c 12 x 2c 2 x 5c 1 x 50c 
Combination 2375: 16 x 1c 12 x 2c 6 x 10c 
Combination 2376: 16 x 1c 12 x 2c 4 x 10c 1 x 20c 
Combination 2377: 16 x 1c 12 x 2c 2 x 10c 2 x 20c 
Combination 2378: 16 x 1c 12 x 2c 1 x 10c 1 x 50c 
Combination 2379: 16 x 1c 12 x 2c 3 x 20c 
Combination 2380: 16 x 1c 7 x 2c 14 x 5c 
Combination 2381: 16 x 1c 7 x 2c 12 x 5c 1 x 10c 
Combination 2382: 16 x 1c 7 x 2c 10 x 5c 2 x 10c 
Combination 2383: 16 x 1c 7 x 2c 10 x 5c 1 x 20c 
Combination 2384: 16 x 1c 7 x 2c 8 x 5c 3 x 10c 
Combination 2385: 16 x 1c 7 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 2386: 16 x 1c 7 x 2c 6 x 5c 4 x 10c 
Combination 2387: 16 x 1c 7 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 2388: 16 x 1c 7 x 2c 6 x 5c 2 x 20c 
Combination 2389: 16 x 1c 7 x 2c 4 x 5c 5 x 10c 
Combination 2390: 16 x 1c 7 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 2391: 16 x 1c 7 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 2392: 16 x 1c 7 x 2c 4 x 5c 1 x 50c 
Combination 2393: 16 x 1c 7 x 2c 2 x 5c 6 x 10c 
Combination 2394: 16 x 1c 7 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 2395: 16 x 1c 7 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 2396: 16 x 1c 7 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 2397: 16 x 1c 7 x 2c 2 x 5c 3 x 20c 
Combination 2398: 16 x 1c 7 x 2c 7 x 10c 
Combination 2399: 16 x 1c 7 x 2c 5 x 10c 1 x 20c 
Combination 2400: 16 x 1c 7 x 2c 3 x 10c 2 x 20c 
Combination 2401: 16 x 1c 7 x 2c 2 x 10c 1 x 50c 
Combination 2402: 16 x 1c 7 x 2c 1 x 10c 3 x 20c 
Combination 2403: 16 x 1c 7 x 2c 1 x 20c 1 x 50c 
Combination 2404: 16 x 1c 2 x 2c 16 x 5c 
Combination 2405: 16 x 1c 2 x 2c 14 x 5c 1 x 10c 
Combination 2406: 16 x 1c 2 x 2c 12 x 5c 2 x 10c 
Combination 2407: 16 x 1c 2 x 2c 12 x 5c 1 x 20c 
Combination 2408: 16 x 1c 2 x 2c 10 x 5c 3 x 10c 
Combination 2409: 16 x 1c 2 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 2410: 16 x 1c 2 x 2c 8 x 5c 4 x 10c 
Combination 2411: 16 x 1c 2 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 2412: 16 x 1c 2 x 2c 8 x 5c 2 x 20c 
Combination 2413: 16 x 1c 2 x 2c 6 x 5c 5 x 10c 
Combination 2414: 16 x 1c 2 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 2415: 16 x 1c 2 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 2416: 16 x 1c 2 x 2c 6 x 5c 1 x 50c 
Combination 2417: 16 x 1c 2 x 2c 4 x 5c 6 x 10c 
Combination 2418: 16 x 1c 2 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 2419: 16 x 1c 2 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 2420: 16 x 1c 2 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 2421: 16 x 1c 2 x 2c 4 x 5c 3 x 20c 
Combination 2422: 16 x 1c 2 x 2c 2 x 5c 7 x 10c 
Combination 2423: 16 x 1c 2 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 2424: 16 x 1c 2 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 2425: 16 x 1c 2 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 2426: 16 x 1c 2 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 2427: 16 x 1c 2 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 2428: 16 x 1c 2 x 2c 8 x 10c 
Combination 2429: 16 x 1c 2 x 2c 6 x 10c 1 x 20c 
Combination 2430: 16 x 1c 2 x 2c 4 x 10c 2 x 20c 
Combination 2431: 16 x 1c 2 x 2c 3 x 10c 1 x 50c 
Combination 2432: 16 x 1c 2 x 2c 2 x 10c 3 x 20c 
Combination 2433: 16 x 1c 2 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 2434: 16 x 1c 2 x 2c 4 x 20c 
Combination 2435: 15 x 1c 40 x 2c 1 x 5c 
Combination 2436: 15 x 1c 35 x 2c 3 x 5c 
Combination 2437: 15 x 1c 35 x 2c 1 x 5c 1 x 10c 
Combination 2438: 15 x 1c 30 x 2c 5 x 5c 
Combination 2439: 15 x 1c 30 x 2c 3 x 5c 1 x 10c 
Combination 2440: 15 x 1c 30 x 2c 1 x 5c 2 x 10c 
Combination 2441: 15 x 1c 30 x 2c 1 x 5c 1 x 20c 
Combination 2442: 15 x 1c 25 x 2c 7 x 5c 
Combination 2443: 15 x 1c 25 x 2c 5 x 5c 1 x 10c 
Combination 2444: 15 x 1c 25 x 2c 3 x 5c 2 x 10c 
Combination 2445: 15 x 1c 25 x 2c 3 x 5c 1 x 20c 
Combination 2446: 15 x 1c 25 x 2c 1 x 5c 3 x 10c 
Combination 2447: 15 x 1c 25 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 2448: 15 x 1c 20 x 2c 9 x 5c 
Combination 2449: 15 x 1c 20 x 2c 7 x 5c 1 x 10c 
Combination 2450: 15 x 1c 20 x 2c 5 x 5c 2 x 10c 
Combination 2451: 15 x 1c 20 x 2c 5 x 5c 1 x 20c 
Combination 2452: 15 x 1c 20 x 2c 3 x 5c 3 x 10c 
Combination 2453: 15 x 1c 20 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 2454: 15 x 1c 20 x 2c 1 x 5c 4 x 10c 
Combination 2455: 15 x 1c 20 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 2456: 15 x 1c 20 x 2c 1 x 5c 2 x 20c 
Combination 2457: 15 x 1c 15 x 2c 11 x 5c 
Combination 2458: 15 x 1c 15 x 2c 9 x 5c 1 x 10c 
Combination 2459: 15 x 1c 15 x 2c 7 x 5c 2 x 10c 
Combination 2460: 15 x 1c 15 x 2c 7 x 5c 1 x 20c 
Combination 2461: 15 x 1c 15 x 2c 5 x 5c 3 x 10c 
Combination 2462: 15 x 1c 15 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 2463: 15 x 1c 15 x 2c 3 x 5c 4 x 10c 
Combination 2464: 15 x 1c 15 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 2465: 15 x 1c 15 x 2c 3 x 5c 2 x 20c 
Combination 2466: 15 x 1c 15 x 2c 1 x 5c 5 x 10c 
Combination 2467: 15 x 1c 15 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 2468: 15 x 1c 15 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 2469: 15 x 1c 15 x 2c 1 x 5c 1 x 50c 
Combination 2470: 15 x 1c 10 x 2c 13 x 5c 
Combination 2471: 15 x 1c 10 x 2c 11 x 5c 1 x 10c 
Combination 2472: 15 x 1c 10 x 2c 9 x 5c 2 x 10c 
Combination 2473: 15 x 1c 10 x 2c 9 x 5c 1 x 20c 
Combination 2474: 15 x 1c 10 x 2c 7 x 5c 3 x 10c 
Combination 2475: 15 x 1c 10 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 2476: 15 x 1c 10 x 2c 5 x 5c 4 x 10c 
Combination 2477: 15 x 1c 10 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 2478: 15 x 1c 10 x 2c 5 x 5c 2 x 20c 
Combination 2479: 15 x 1c 10 x 2c 3 x 5c 5 x 10c 
Combination 2480: 15 x 1c 10 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 2481: 15 x 1c 10 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 2482: 15 x 1c 10 x 2c 3 x 5c 1 x 50c 
Combination 2483: 15 x 1c 10 x 2c 1 x 5c 6 x 10c 
Combination 2484: 15 x 1c 10 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 2485: 15 x 1c 10 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 2486: 15 x 1c 10 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 2487: 15 x 1c 10 x 2c 1 x 5c 3 x 20c 
Combination 2488: 15 x 1c 5 x 2c 15 x 5c 
Combination 2489: 15 x 1c 5 x 2c 13 x 5c 1 x 10c 
Combination 2490: 15 x 1c 5 x 2c 11 x 5c 2 x 10c 
Combination 2491: 15 x 1c 5 x 2c 11 x 5c 1 x 20c 
Combination 2492: 15 x 1c 5 x 2c 9 x 5c 3 x 10c 
Combination 2493: 15 x 1c 5 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 2494: 15 x 1c 5 x 2c 7 x 5c 4 x 10c 
Combination 2495: 15 x 1c 5 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 2496: 15 x 1c 5 x 2c 7 x 5c 2 x 20c 
Combination 2497: 15 x 1c 5 x 2c 5 x 5c 5 x 10c 
Combination 2498: 15 x 1c 5 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 2499: 15 x 1c 5 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 2500: 15 x 1c 5 x 2c 5 x 5c 1 x 50c 
Combination 2501: 15 x 1c 5 x 2c 3 x 5c 6 x 10c 
Combination 2502: 15 x 1c 5 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 2503: 15 x 1c 5 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 2504: 15 x 1c 5 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 2505: 15 x 1c 5 x 2c 3 x 5c 3 x 20c 
Combination 2506: 15 x 1c 5 x 2c 1 x 5c 7 x 10c 
Combination 2507: 15 x 1c 5 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 2508: 15 x 1c 5 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 2509: 15 x 1c 5 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 2510: 15 x 1c 5 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 2511: 15 x 1c 5 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 2512: 15 x 1c 17 x 5c 
Combination 2513: 15 x 1c 15 x 5c 1 x 10c 
Combination 2514: 15 x 1c 13 x 5c 2 x 10c 
Combination 2515: 15 x 1c 13 x 5c 1 x 20c 
Combination 2516: 15 x 1c 11 x 5c 3 x 10c 
Combination 2517: 15 x 1c 11 x 5c 1 x 10c 1 x 20c 
Combination 2518: 15 x 1c 9 x 5c 4 x 10c 
Combination 2519: 15 x 1c 9 x 5c 2 x 10c 1 x 20c 
Combination 2520: 15 x 1c 9 x 5c 2 x 20c 
Combination 2521: 15 x 1c 7 x 5c 5 x 10c 
Combination 2522: 15 x 1c 7 x 5c 3 x 10c 1 x 20c 
Combination 2523: 15 x 1c 7 x 5c 1 x 10c 2 x 20c 
Combination 2524: 15 x 1c 7 x 5c 1 x 50c 
Combination 2525: 15 x 1c 5 x 5c 6 x 10c 
Combination 2526: 15 x 1c 5 x 5c 4 x 10c 1 x 20c 
Combination 2527: 15 x 1c 5 x 5c 2 x 10c 2 x 20c 
Combination 2528: 15 x 1c 5 x 5c 1 x 10c 1 x 50c 
Combination 2529: 15 x 1c 5 x 5c 3 x 20c 
Combination 2530: 15 x 1c 3 x 5c 7 x 10c 
Combination 2531: 15 x 1c 3 x 5c 5 x 10c 1 x 20c 
Combination 2532: 15 x 1c 3 x 5c 3 x 10c 2 x 20c 
Combination 2533: 15 x 1c 3 x 5c 2 x 10c 1 x 50c 
Combination 2534: 15 x 1c 3 x 5c 1 x 10c 3 x 20c 
Combination 2535: 15 x 1c 3 x 5c 1 x 20c 1 x 50c 
Combination 2536: 15 x 1c 1 x 5c 8 x 10c 
Combination 2537: 15 x 1c 1 x 5c 6 x 10c 1 x 20c 
Combination 2538: 15 x 1c 1 x 5c 4 x 10c 2 x 20c 
Combination 2539: 15 x 1c 1 x 5c 3 x 10c 1 x 50c 
Combination 2540: 15 x 1c 1 x 5c 2 x 10c 3 x 20c 
Combination 2541: 15 x 1c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 2542: 15 x 1c 1 x 5c 4 x 20c 
Combination 2543: 14 x 1c 43 x 2c 
Combination 2544: 14 x 1c 38 x 2c 2 x 5c 
Combination 2545: 14 x 1c 38 x 2c 1 x 10c 
Combination 2546: 14 x 1c 33 x 2c 4 x 5c 
Combination 2547: 14 x 1c 33 x 2c 2 x 5c 1 x 10c 
Combination 2548: 14 x 1c 33 x 2c 2 x 10c 
Combination 2549: 14 x 1c 33 x 2c 1 x 20c 
Combination 2550: 14 x 1c 28 x 2c 6 x 5c 
Combination 2551: 14 x 1c 28 x 2c 4 x 5c 1 x 10c 
Combination 2552: 14 x 1c 28 x 2c 2 x 5c 2 x 10c 
Combination 2553: 14 x 1c 28 x 2c 2 x 5c 1 x 20c 
Combination 2554: 14 x 1c 28 x 2c 3 x 10c 
Combination 2555: 14 x 1c 28 x 2c 1 x 10c 1 x 20c 
Combination 2556: 14 x 1c 23 x 2c 8 x 5c 
Combination 2557: 14 x 1c 23 x 2c 6 x 5c 1 x 10c 
Combination 2558: 14 x 1c 23 x 2c 4 x 5c 2 x 10c 
Combination 2559: 14 x 1c 23 x 2c 4 x 5c 1 x 20c 
Combination 2560: 14 x 1c 23 x 2c 2 x 5c 3 x 10c 
Combination 2561: 14 x 1c 23 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 2562: 14 x 1c 23 x 2c 4 x 10c 
Combination 2563: 14 x 1c 23 x 2c 2 x 10c 1 x 20c 
Combination 2564: 14 x 1c 23 x 2c 2 x 20c 
Combination 2565: 14 x 1c 18 x 2c 10 x 5c 
Combination 2566: 14 x 1c 18 x 2c 8 x 5c 1 x 10c 
Combination 2567: 14 x 1c 18 x 2c 6 x 5c 2 x 10c 
Combination 2568: 14 x 1c 18 x 2c 6 x 5c 1 x 20c 
Combination 2569: 14 x 1c 18 x 2c 4 x 5c 3 x 10c 
Combination 2570: 14 x 1c 18 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 2571: 14 x 1c 18 x 2c 2 x 5c 4 x 10c 
Combination 2572: 14 x 1c 18 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 2573: 14 x 1c 18 x 2c 2 x 5c 2 x 20c 
Combination 2574: 14 x 1c 18 x 2c 5 x 10c 
Combination 2575: 14 x 1c 18 x 2c 3 x 10c 1 x 20c 
Combination 2576: 14 x 1c 18 x 2c 1 x 10c 2 x 20c 
Combination 2577: 14 x 1c 18 x 2c 1 x 50c 
Combination 2578: 14 x 1c 13 x 2c 12 x 5c 
Combination 2579: 14 x 1c 13 x 2c 10 x 5c 1 x 10c 
Combination 2580: 14 x 1c 13 x 2c 8 x 5c 2 x 10c 
Combination 2581: 14 x 1c 13 x 2c 8 x 5c 1 x 20c 
Combination 2582: 14 x 1c 13 x 2c 6 x 5c 3 x 10c 
Combination 2583: 14 x 1c 13 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 2584: 14 x 1c 13 x 2c 4 x 5c 4 x 10c 
Combination 2585: 14 x 1c 13 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 2586: 14 x 1c 13 x 2c 4 x 5c 2 x 20c 
Combination 2587: 14 x 1c 13 x 2c 2 x 5c 5 x 10c 
Combination 2588: 14 x 1c 13 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 2589: 14 x 1c 13 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 2590: 14 x 1c 13 x 2c 2 x 5c 1 x 50c 
Combination 2591: 14 x 1c 13 x 2c 6 x 10c 
Combination 2592: 14 x 1c 13 x 2c 4 x 10c 1 x 20c 
Combination 2593: 14 x 1c 13 x 2c 2 x 10c 2 x 20c 
Combination 2594: 14 x 1c 13 x 2c 1 x 10c 1 x 50c 
Combination 2595: 14 x 1c 13 x 2c 3 x 20c 
Combination 2596: 14 x 1c 8 x 2c 14 x 5c 
Combination 2597: 14 x 1c 8 x 2c 12 x 5c 1 x 10c 
Combination 2598: 14 x 1c 8 x 2c 10 x 5c 2 x 10c 
Combination 2599: 14 x 1c 8 x 2c 10 x 5c 1 x 20c 
Combination 2600: 14 x 1c 8 x 2c 8 x 5c 3 x 10c 
Combination 2601: 14 x 1c 8 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 2602: 14 x 1c 8 x 2c 6 x 5c 4 x 10c 
Combination 2603: 14 x 1c 8 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 2604: 14 x 1c 8 x 2c 6 x 5c 2 x 20c 
Combination 2605: 14 x 1c 8 x 2c 4 x 5c 5 x 10c 
Combination 2606: 14 x 1c 8 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 2607: 14 x 1c 8 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 2608: 14 x 1c 8 x 2c 4 x 5c 1 x 50c 
Combination 2609: 14 x 1c 8 x 2c 2 x 5c 6 x 10c 
Combination 2610: 14 x 1c 8 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 2611: 14 x 1c 8 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 2612: 14 x 1c 8 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 2613: 14 x 1c 8 x 2c 2 x 5c 3 x 20c 
Combination 2614: 14 x 1c 8 x 2c 7 x 10c 
Combination 2615: 14 x 1c 8 x 2c 5 x 10c 1 x 20c 
Combination 2616: 14 x 1c 8 x 2c 3 x 10c 2 x 20c 
Combination 2617: 14 x 1c 8 x 2c 2 x 10c 1 x 50c 
Combination 2618: 14 x 1c 8 x 2c 1 x 10c 3 x 20c 
Combination 2619: 14 x 1c 8 x 2c 1 x 20c 1 x 50c 
Combination 2620: 14 x 1c 3 x 2c 16 x 5c 
Combination 2621: 14 x 1c 3 x 2c 14 x 5c 1 x 10c 
Combination 2622: 14 x 1c 3 x 2c 12 x 5c 2 x 10c 
Combination 2623: 14 x 1c 3 x 2c 12 x 5c 1 x 20c 
Combination 2624: 14 x 1c 3 x 2c 10 x 5c 3 x 10c 
Combination 2625: 14 x 1c 3 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 2626: 14 x 1c 3 x 2c 8 x 5c 4 x 10c 
Combination 2627: 14 x 1c 3 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 2628: 14 x 1c 3 x 2c 8 x 5c 2 x 20c 
Combination 2629: 14 x 1c 3 x 2c 6 x 5c 5 x 10c 
Combination 2630: 14 x 1c 3 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 2631: 14 x 1c 3 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 2632: 14 x 1c 3 x 2c 6 x 5c 1 x 50c 
Combination 2633: 14 x 1c 3 x 2c 4 x 5c 6 x 10c 
Combination 2634: 14 x 1c 3 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 2635: 14 x 1c 3 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 2636: 14 x 1c 3 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 2637: 14 x 1c 3 x 2c 4 x 5c 3 x 20c 
Combination 2638: 14 x 1c 3 x 2c 2 x 5c 7 x 10c 
Combination 2639: 14 x 1c 3 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 2640: 14 x 1c 3 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 2641: 14 x 1c 3 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 2642: 14 x 1c 3 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 2643: 14 x 1c 3 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 2644: 14 x 1c 3 x 2c 8 x 10c 
Combination 2645: 14 x 1c 3 x 2c 6 x 10c 1 x 20c 
Combination 2646: 14 x 1c 3 x 2c 4 x 10c 2 x 20c 
Combination 2647: 14 x 1c 3 x 2c 3 x 10c 1 x 50c 
Combination 2648: 14 x 1c 3 x 2c 2 x 10c 3 x 20c 
Combination 2649: 14 x 1c 3 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 2650: 14 x 1c 3 x 2c 4 x 20c 
Combination 2651: 13 x 1c 41 x 2c 1 x 5c 
Combination 2652: 13 x 1c 36 x 2c 3 x 5c 
Combination 2653: 13 x 1c 36 x 2c 1 x 5c 1 x 10c 
Combination 2654: 13 x 1c 31 x 2c 5 x 5c 
Combination 2655: 13 x 1c 31 x 2c 3 x 5c 1 x 10c 
Combination 2656: 13 x 1c 31 x 2c 1 x 5c 2 x 10c 
Combination 2657: 13 x 1c 31 x 2c 1 x 5c 1 x 20c 
Combination 2658: 13 x 1c 26 x 2c 7 x 5c 
Combination 2659: 13 x 1c 26 x 2c 5 x 5c 1 x 10c 
Combination 2660: 13 x 1c 26 x 2c 3 x 5c 2 x 10c 
Combination 2661: 13 x 1c 26 x 2c 3 x 5c 1 x 20c 
Combination 2662: 13 x 1c 26 x 2c 1 x 5c 3 x 10c 
Combination 2663: 13 x 1c 26 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 2664: 13 x 1c 21 x 2c 9 x 5c 
Combination 2665: 13 x 1c 21 x 2c 7 x 5c 1 x 10c 
Combination 2666: 13 x 1c 21 x 2c 5 x 5c 2 x 10c 
Combination 2667: 13 x 1c 21 x 2c 5 x 5c 1 x 20c 
Combination 2668: 13 x 1c 21 x 2c 3 x 5c 3 x 10c 
Combination 2669: 13 x 1c 21 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 2670: 13 x 1c 21 x 2c 1 x 5c 4 x 10c 
Combination 2671: 13 x 1c 21 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 2672: 13 x 1c 21 x 2c 1 x 5c 2 x 20c 
Combination 2673: 13 x 1c 16 x 2c 11 x 5c 
Combination 2674: 13 x 1c 16 x 2c 9 x 5c 1 x 10c 
Combination 2675: 13 x 1c 16 x 2c 7 x 5c 2 x 10c 
Combination 2676: 13 x 1c 16 x 2c 7 x 5c 1 x 20c 
Combination 2677: 13 x 1c 16 x 2c 5 x 5c 3 x 10c 
Combination 2678: 13 x 1c 16 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 2679: 13 x 1c 16 x 2c 3 x 5c 4 x 10c 
Combination 2680: 13 x 1c 16 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 2681: 13 x 1c 16 x 2c 3 x 5c 2 x 20c 
Combination 2682: 13 x 1c 16 x 2c 1 x 5c 5 x 10c 
Combination 2683: 13 x 1c 16 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 2684: 13 x 1c 16 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 2685: 13 x 1c 16 x 2c 1 x 5c 1 x 50c 
Combination 2686: 13 x 1c 11 x 2c 13 x 5c 
Combination 2687: 13 x 1c 11 x 2c 11 x 5c 1 x 10c 
Combination 2688: 13 x 1c 11 x 2c 9 x 5c 2 x 10c 
Combination 2689: 13 x 1c 11 x 2c 9 x 5c 1 x 20c 
Combination 2690: 13 x 1c 11 x 2c 7 x 5c 3 x 10c 
Combination 2691: 13 x 1c 11 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 2692: 13 x 1c 11 x 2c 5 x 5c 4 x 10c 
Combination 2693: 13 x 1c 11 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 2694: 13 x 1c 11 x 2c 5 x 5c 2 x 20c 
Combination 2695: 13 x 1c 11 x 2c 3 x 5c 5 x 10c 
Combination 2696: 13 x 1c 11 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 2697: 13 x 1c 11 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 2698: 13 x 1c 11 x 2c 3 x 5c 1 x 50c 
Combination 2699: 13 x 1c 11 x 2c 1 x 5c 6 x 10c 
Combination 2700: 13 x 1c 11 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 2701: 13 x 1c 11 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 2702: 13 x 1c 11 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 2703: 13 x 1c 11 x 2c 1 x 5c 3 x 20c 
Combination 2704: 13 x 1c 6 x 2c 15 x 5c 
Combination 2705: 13 x 1c 6 x 2c 13 x 5c 1 x 10c 
Combination 2706: 13 x 1c 6 x 2c 11 x 5c 2 x 10c 
Combination 2707: 13 x 1c 6 x 2c 11 x 5c 1 x 20c 
Combination 2708: 13 x 1c 6 x 2c 9 x 5c 3 x 10c 
Combination 2709: 13 x 1c 6 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 2710: 13 x 1c 6 x 2c 7 x 5c 4 x 10c 
Combination 2711: 13 x 1c 6 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 2712: 13 x 1c 6 x 2c 7 x 5c 2 x 20c 
Combination 2713: 13 x 1c 6 x 2c 5 x 5c 5 x 10c 
Combination 2714: 13 x 1c 6 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 2715: 13 x 1c 6 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 2716: 13 x 1c 6 x 2c 5 x 5c 1 x 50c 
Combination 2717: 13 x 1c 6 x 2c 3 x 5c 6 x 10c 
Combination 2718: 13 x 1c 6 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 2719: 13 x 1c 6 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 2720: 13 x 1c 6 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 2721: 13 x 1c 6 x 2c 3 x 5c 3 x 20c 
Combination 2722: 13 x 1c 6 x 2c 1 x 5c 7 x 10c 
Combination 2723: 13 x 1c 6 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 2724: 13 x 1c 6 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 2725: 13 x 1c 6 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 2726: 13 x 1c 6 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 2727: 13 x 1c 6 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 2728: 13 x 1c 1 x 2c 17 x 5c 
Combination 2729: 13 x 1c 1 x 2c 15 x 5c 1 x 10c 
Combination 2730: 13 x 1c 1 x 2c 13 x 5c 2 x 10c 
Combination 2731: 13 x 1c 1 x 2c 13 x 5c 1 x 20c 
Combination 2732: 13 x 1c 1 x 2c 11 x 5c 3 x 10c 
Combination 2733: 13 x 1c 1 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 2734: 13 x 1c 1 x 2c 9 x 5c 4 x 10c 
Combination 2735: 13 x 1c 1 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 2736: 13 x 1c 1 x 2c 9 x 5c 2 x 20c 
Combination 2737: 13 x 1c 1 x 2c 7 x 5c 5 x 10c 
Combination 2738: 13 x 1c 1 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 2739: 13 x 1c 1 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 2740: 13 x 1c 1 x 2c 7 x 5c 1 x 50c 
Combination 2741: 13 x 1c 1 x 2c 5 x 5c 6 x 10c 
Combination 2742: 13 x 1c 1 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 2743: 13 x 1c 1 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 2744: 13 x 1c 1 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 2745: 13 x 1c 1 x 2c 5 x 5c 3 x 20c 
Combination 2746: 13 x 1c 1 x 2c 3 x 5c 7 x 10c 
Combination 2747: 13 x 1c 1 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 2748: 13 x 1c 1 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 2749: 13 x 1c 1 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 2750: 13 x 1c 1 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 2751: 13 x 1c 1 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 2752: 13 x 1c 1 x 2c 1 x 5c 8 x 10c 
Combination 2753: 13 x 1c 1 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 2754: 13 x 1c 1 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 2755: 13 x 1c 1 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 2756: 13 x 1c 1 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 2757: 13 x 1c 1 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 2758: 13 x 1c 1 x 2c 1 x 5c 4 x 20c 
Combination 2759: 12 x 1c 44 x 2c 
Combination 2760: 12 x 1c 39 x 2c 2 x 5c 
Combination 2761: 12 x 1c 39 x 2c 1 x 10c 
Combination 2762: 12 x 1c 34 x 2c 4 x 5c 
Combination 2763: 12 x 1c 34 x 2c 2 x 5c 1 x 10c 
Combination 2764: 12 x 1c 34 x 2c 2 x 10c 
Combination 2765: 12 x 1c 34 x 2c 1 x 20c 
Combination 2766: 12 x 1c 29 x 2c 6 x 5c 
Combination 2767: 12 x 1c 29 x 2c 4 x 5c 1 x 10c 
Combination 2768: 12 x 1c 29 x 2c 2 x 5c 2 x 10c 
Combination 2769: 12 x 1c 29 x 2c 2 x 5c 1 x 20c 
Combination 2770: 12 x 1c 29 x 2c 3 x 10c 
Combination 2771: 12 x 1c 29 x 2c 1 x 10c 1 x 20c 
Combination 2772: 12 x 1c 24 x 2c 8 x 5c 
Combination 2773: 12 x 1c 24 x 2c 6 x 5c 1 x 10c 
Combination 2774: 12 x 1c 24 x 2c 4 x 5c 2 x 10c 
Combination 2775: 12 x 1c 24 x 2c 4 x 5c 1 x 20c 
Combination 2776: 12 x 1c 24 x 2c 2 x 5c 3 x 10c 
Combination 2777: 12 x 1c 24 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 2778: 12 x 1c 24 x 2c 4 x 10c 
Combination 2779: 12 x 1c 24 x 2c 2 x 10c 1 x 20c 
Combination 2780: 12 x 1c 24 x 2c 2 x 20c 
Combination 2781: 12 x 1c 19 x 2c 10 x 5c 
Combination 2782: 12 x 1c 19 x 2c 8 x 5c 1 x 10c 
Combination 2783: 12 x 1c 19 x 2c 6 x 5c 2 x 10c 
Combination 2784: 12 x 1c 19 x 2c 6 x 5c 1 x 20c 
Combination 2785: 12 x 1c 19 x 2c 4 x 5c 3 x 10c 
Combination 2786: 12 x 1c 19 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 2787: 12 x 1c 19 x 2c 2 x 5c 4 x 10c 
Combination 2788: 12 x 1c 19 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 2789: 12 x 1c 19 x 2c 2 x 5c 2 x 20c 
Combination 2790: 12 x 1c 19 x 2c 5 x 10c 
Combination 2791: 12 x 1c 19 x 2c 3 x 10c 1 x 20c 
Combination 2792: 12 x 1c 19 x 2c 1 x 10c 2 x 20c 
Combination 2793: 12 x 1c 19 x 2c 1 x 50c 
Combination 2794: 12 x 1c 14 x 2c 12 x 5c 
Combination 2795: 12 x 1c 14 x 2c 10 x 5c 1 x 10c 
Combination 2796: 12 x 1c 14 x 2c 8 x 5c 2 x 10c 
Combination 2797: 12 x 1c 14 x 2c 8 x 5c 1 x 20c 
Combination 2798: 12 x 1c 14 x 2c 6 x 5c 3 x 10c 
Combination 2799: 12 x 1c 14 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 2800: 12 x 1c 14 x 2c 4 x 5c 4 x 10c 
Combination 2801: 12 x 1c 14 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 2802: 12 x 1c 14 x 2c 4 x 5c 2 x 20c 
Combination 2803: 12 x 1c 14 x 2c 2 x 5c 5 x 10c 
Combination 2804: 12 x 1c 14 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 2805: 12 x 1c 14 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 2806: 12 x 1c 14 x 2c 2 x 5c 1 x 50c 
Combination 2807: 12 x 1c 14 x 2c 6 x 10c 
Combination 2808: 12 x 1c 14 x 2c 4 x 10c 1 x 20c 
Combination 2809: 12 x 1c 14 x 2c 2 x 10c 2 x 20c 
Combination 2810: 12 x 1c 14 x 2c 1 x 10c 1 x 50c 
Combination 2811: 12 x 1c 14 x 2c 3 x 20c 
Combination 2812: 12 x 1c 9 x 2c 14 x 5c 
Combination 2813: 12 x 1c 9 x 2c 12 x 5c 1 x 10c 
Combination 2814: 12 x 1c 9 x 2c 10 x 5c 2 x 10c 
Combination 2815: 12 x 1c 9 x 2c 10 x 5c 1 x 20c 
Combination 2816: 12 x 1c 9 x 2c 8 x 5c 3 x 10c 
Combination 2817: 12 x 1c 9 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 2818: 12 x 1c 9 x 2c 6 x 5c 4 x 10c 
Combination 2819: 12 x 1c 9 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 2820: 12 x 1c 9 x 2c 6 x 5c 2 x 20c 
Combination 2821: 12 x 1c 9 x 2c 4 x 5c 5 x 10c 
Combination 2822: 12 x 1c 9 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 2823: 12 x 1c 9 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 2824: 12 x 1c 9 x 2c 4 x 5c 1 x 50c 
Combination 2825: 12 x 1c 9 x 2c 2 x 5c 6 x 10c 
Combination 2826: 12 x 1c 9 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 2827: 12 x 1c 9 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 2828: 12 x 1c 9 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 2829: 12 x 1c 9 x 2c 2 x 5c 3 x 20c 
Combination 2830: 12 x 1c 9 x 2c 7 x 10c 
Combination 2831: 12 x 1c 9 x 2c 5 x 10c 1 x 20c 
Combination 2832: 12 x 1c 9 x 2c 3 x 10c 2 x 20c 
Combination 2833: 12 x 1c 9 x 2c 2 x 10c 1 x 50c 
Combination 2834: 12 x 1c 9 x 2c 1 x 10c 3 x 20c 
Combination 2835: 12 x 1c 9 x 2c 1 x 20c 1 x 50c 
Combination 2836: 12 x 1c 4 x 2c 16 x 5c 
Combination 2837: 12 x 1c 4 x 2c 14 x 5c 1 x 10c 
Combination 2838: 12 x 1c 4 x 2c 12 x 5c 2 x 10c 
Combination 2839: 12 x 1c 4 x 2c 12 x 5c 1 x 20c 
Combination 2840: 12 x 1c 4 x 2c 10 x 5c 3 x 10c 
Combination 2841: 12 x 1c 4 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 2842: 12 x 1c 4 x 2c 8 x 5c 4 x 10c 
Combination 2843: 12 x 1c 4 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 2844: 12 x 1c 4 x 2c 8 x 5c 2 x 20c 
Combination 2845: 12 x 1c 4 x 2c 6 x 5c 5 x 10c 
Combination 2846: 12 x 1c 4 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 2847: 12 x 1c 4 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 2848: 12 x 1c 4 x 2c 6 x 5c 1 x 50c 
Combination 2849: 12 x 1c 4 x 2c 4 x 5c 6 x 10c 
Combination 2850: 12 x 1c 4 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 2851: 12 x 1c 4 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 2852: 12 x 1c 4 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 2853: 12 x 1c 4 x 2c 4 x 5c 3 x 20c 
Combination 2854: 12 x 1c 4 x 2c 2 x 5c 7 x 10c 
Combination 2855: 12 x 1c 4 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 2856: 12 x 1c 4 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 2857: 12 x 1c 4 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 2858: 12 x 1c 4 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 2859: 12 x 1c 4 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 2860: 12 x 1c 4 x 2c 8 x 10c 
Combination 2861: 12 x 1c 4 x 2c 6 x 10c 1 x 20c 
Combination 2862: 12 x 1c 4 x 2c 4 x 10c 2 x 20c 
Combination 2863: 12 x 1c 4 x 2c 3 x 10c 1 x 50c 
Combination 2864: 12 x 1c 4 x 2c 2 x 10c 3 x 20c 
Combination 2865: 12 x 1c 4 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 2866: 12 x 1c 4 x 2c 4 x 20c 
Combination 2867: 11 x 1c 42 x 2c 1 x 5c 
Combination 2868: 11 x 1c 37 x 2c 3 x 5c 
Combination 2869: 11 x 1c 37 x 2c 1 x 5c 1 x 10c 
Combination 2870: 11 x 1c 32 x 2c 5 x 5c 
Combination 2871: 11 x 1c 32 x 2c 3 x 5c 1 x 10c 
Combination 2872: 11 x 1c 32 x 2c 1 x 5c 2 x 10c 
Combination 2873: 11 x 1c 32 x 2c 1 x 5c 1 x 20c 
Combination 2874: 11 x 1c 27 x 2c 7 x 5c 
Combination 2875: 11 x 1c 27 x 2c 5 x 5c 1 x 10c 
Combination 2876: 11 x 1c 27 x 2c 3 x 5c 2 x 10c 
Combination 2877: 11 x 1c 27 x 2c 3 x 5c 1 x 20c 
Combination 2878: 11 x 1c 27 x 2c 1 x 5c 3 x 10c 
Combination 2879: 11 x 1c 27 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 2880: 11 x 1c 22 x 2c 9 x 5c 
Combination 2881: 11 x 1c 22 x 2c 7 x 5c 1 x 10c 
Combination 2882: 11 x 1c 22 x 2c 5 x 5c 2 x 10c 
Combination 2883: 11 x 1c 22 x 2c 5 x 5c 1 x 20c 
Combination 2884: 11 x 1c 22 x 2c 3 x 5c 3 x 10c 
Combination 2885: 11 x 1c 22 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 2886: 11 x 1c 22 x 2c 1 x 5c 4 x 10c 
Combination 2887: 11 x 1c 22 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 2888: 11 x 1c 22 x 2c 1 x 5c 2 x 20c 
Combination 2889: 11 x 1c 17 x 2c 11 x 5c 
Combination 2890: 11 x 1c 17 x 2c 9 x 5c 1 x 10c 
Combination 2891: 11 x 1c 17 x 2c 7 x 5c 2 x 10c 
Combination 2892: 11 x 1c 17 x 2c 7 x 5c 1 x 20c 
Combination 2893: 11 x 1c 17 x 2c 5 x 5c 3 x 10c 
Combination 2894: 11 x 1c 17 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 2895: 11 x 1c 17 x 2c 3 x 5c 4 x 10c 
Combination 2896: 11 x 1c 17 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 2897: 11 x 1c 17 x 2c 3 x 5c 2 x 20c 
Combination 2898: 11 x 1c 17 x 2c 1 x 5c 5 x 10c 
Combination 2899: 11 x 1c 17 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 2900: 11 x 1c 17 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 2901: 11 x 1c 17 x 2c 1 x 5c 1 x 50c 
Combination 2902: 11 x 1c 12 x 2c 13 x 5c 
Combination 2903: 11 x 1c 12 x 2c 11 x 5c 1 x 10c 
Combination 2904: 11 x 1c 12 x 2c 9 x 5c 2 x 10c 
Combination 2905: 11 x 1c 12 x 2c 9 x 5c 1 x 20c 
Combination 2906: 11 x 1c 12 x 2c 7 x 5c 3 x 10c 
Combination 2907: 11 x 1c 12 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 2908: 11 x 1c 12 x 2c 5 x 5c 4 x 10c 
Combination 2909: 11 x 1c 12 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 2910: 11 x 1c 12 x 2c 5 x 5c 2 x 20c 
Combination 2911: 11 x 1c 12 x 2c 3 x 5c 5 x 10c 
Combination 2912: 11 x 1c 12 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 2913: 11 x 1c 12 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 2914: 11 x 1c 12 x 2c 3 x 5c 1 x 50c 
Combination 2915: 11 x 1c 12 x 2c 1 x 5c 6 x 10c 
Combination 2916: 11 x 1c 12 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 2917: 11 x 1c 12 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 2918: 11 x 1c 12 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 2919: 11 x 1c 12 x 2c 1 x 5c 3 x 20c 
Combination 2920: 11 x 1c 7 x 2c 15 x 5c 
Combination 2921: 11 x 1c 7 x 2c 13 x 5c 1 x 10c 
Combination 2922: 11 x 1c 7 x 2c 11 x 5c 2 x 10c 
Combination 2923: 11 x 1c 7 x 2c 11 x 5c 1 x 20c 
Combination 2924: 11 x 1c 7 x 2c 9 x 5c 3 x 10c 
Combination 2925: 11 x 1c 7 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 2926: 11 x 1c 7 x 2c 7 x 5c 4 x 10c 
Combination 2927: 11 x 1c 7 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 2928: 11 x 1c 7 x 2c 7 x 5c 2 x 20c 
Combination 2929: 11 x 1c 7 x 2c 5 x 5c 5 x 10c 
Combination 2930: 11 x 1c 7 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 2931: 11 x 1c 7 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 2932: 11 x 1c 7 x 2c 5 x 5c 1 x 50c 
Combination 2933: 11 x 1c 7 x 2c 3 x 5c 6 x 10c 
Combination 2934: 11 x 1c 7 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 2935: 11 x 1c 7 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 2936: 11 x 1c 7 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 2937: 11 x 1c 7 x 2c 3 x 5c 3 x 20c 
Combination 2938: 11 x 1c 7 x 2c 1 x 5c 7 x 10c 
Combination 2939: 11 x 1c 7 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 2940: 11 x 1c 7 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 2941: 11 x 1c 7 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 2942: 11 x 1c 7 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 2943: 11 x 1c 7 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 2944: 11 x 1c 2 x 2c 17 x 5c 
Combination 2945: 11 x 1c 2 x 2c 15 x 5c 1 x 10c 
Combination 2946: 11 x 1c 2 x 2c 13 x 5c 2 x 10c 
Combination 2947: 11 x 1c 2 x 2c 13 x 5c 1 x 20c 
Combination 2948: 11 x 1c 2 x 2c 11 x 5c 3 x 10c 
Combination 2949: 11 x 1c 2 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 2950: 11 x 1c 2 x 2c 9 x 5c 4 x 10c 
Combination 2951: 11 x 1c 2 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 2952: 11 x 1c 2 x 2c 9 x 5c 2 x 20c 
Combination 2953: 11 x 1c 2 x 2c 7 x 5c 5 x 10c 
Combination 2954: 11 x 1c 2 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 2955: 11 x 1c 2 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 2956: 11 x 1c 2 x 2c 7 x 5c 1 x 50c 
Combination 2957: 11 x 1c 2 x 2c 5 x 5c 6 x 10c 
Combination 2958: 11 x 1c 2 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 2959: 11 x 1c 2 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 2960: 11 x 1c 2 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 2961: 11 x 1c 2 x 2c 5 x 5c 3 x 20c 
Combination 2962: 11 x 1c 2 x 2c 3 x 5c 7 x 10c 
Combination 2963: 11 x 1c 2 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 2964: 11 x 1c 2 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 2965: 11 x 1c 2 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 2966: 11 x 1c 2 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 2967: 11 x 1c 2 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 2968: 11 x 1c 2 x 2c 1 x 5c 8 x 10c 
Combination 2969: 11 x 1c 2 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 2970: 11 x 1c 2 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 2971: 11 x 1c 2 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 2972: 11 x 1c 2 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 2973: 11 x 1c 2 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 2974: 11 x 1c 2 x 2c 1 x 5c 4 x 20c 
Combination 2975: 10 x 1c 45 x 2c 
Combination 2976: 10 x 1c 40 x 2c 2 x 5c 
Combination 2977: 10 x 1c 40 x 2c 1 x 10c 
Combination 2978: 10 x 1c 35 x 2c 4 x 5c 
Combination 2979: 10 x 1c 35 x 2c 2 x 5c 1 x 10c 
Combination 2980: 10 x 1c 35 x 2c 2 x 10c 
Combination 2981: 10 x 1c 35 x 2c 1 x 20c 
Combination 2982: 10 x 1c 30 x 2c 6 x 5c 
Combination 2983: 10 x 1c 30 x 2c 4 x 5c 1 x 10c 
Combination 2984: 10 x 1c 30 x 2c 2 x 5c 2 x 10c 
Combination 2985: 10 x 1c 30 x 2c 2 x 5c 1 x 20c 
Combination 2986: 10 x 1c 30 x 2c 3 x 10c 
Combination 2987: 10 x 1c 30 x 2c 1 x 10c 1 x 20c 
Combination 2988: 10 x 1c 25 x 2c 8 x 5c 
Combination 2989: 10 x 1c 25 x 2c 6 x 5c 1 x 10c 
Combination 2990: 10 x 1c 25 x 2c 4 x 5c 2 x 10c 
Combination 2991: 10 x 1c 25 x 2c 4 x 5c 1 x 20c 
Combination 2992: 10 x 1c 25 x 2c 2 x 5c 3 x 10c 
Combination 2993: 10 x 1c 25 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 2994: 10 x 1c 25 x 2c 4 x 10c 
Combination 2995: 10 x 1c 25 x 2c 2 x 10c 1 x 20c 
Combination 2996: 10 x 1c 25 x 2c 2 x 20c 
Combination 2997: 10 x 1c 20 x 2c 10 x 5c 
Combination 2998: 10 x 1c 20 x 2c 8 x 5c 1 x 10c 
Combination 2999: 10 x 1c 20 x 2c 6 x 5c 2 x 10c 
Combination 3000: 10 x 1c 20 x 2c 6 x 5c 1 x 20c 
Combination 3001: 10 x 1c 20 x 2c 4 x 5c 3 x 10c 
Combination 3002: 10 x 1c 20 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 3003: 10 x 1c 20 x 2c 2 x 5c 4 x 10c 
Combination 3004: 10 x 1c 20 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 3005: 10 x 1c 20 x 2c 2 x 5c 2 x 20c 
Combination 3006: 10 x 1c 20 x 2c 5 x 10c 
Combination 3007: 10 x 1c 20 x 2c 3 x 10c 1 x 20c 
Combination 3008: 10 x 1c 20 x 2c 1 x 10c 2 x 20c 
Combination 3009: 10 x 1c 20 x 2c 1 x 50c 
Combination 3010: 10 x 1c 15 x 2c 12 x 5c 
Combination 3011: 10 x 1c 15 x 2c 10 x 5c 1 x 10c 
Combination 3012: 10 x 1c 15 x 2c 8 x 5c 2 x 10c 
Combination 3013: 10 x 1c 15 x 2c 8 x 5c 1 x 20c 
Combination 3014: 10 x 1c 15 x 2c 6 x 5c 3 x 10c 
Combination 3015: 10 x 1c 15 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 3016: 10 x 1c 15 x 2c 4 x 5c 4 x 10c 
Combination 3017: 10 x 1c 15 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 3018: 10 x 1c 15 x 2c 4 x 5c 2 x 20c 
Combination 3019: 10 x 1c 15 x 2c 2 x 5c 5 x 10c 
Combination 3020: 10 x 1c 15 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 3021: 10 x 1c 15 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 3022: 10 x 1c 15 x 2c 2 x 5c 1 x 50c 
Combination 3023: 10 x 1c 15 x 2c 6 x 10c 
Combination 3024: 10 x 1c 15 x 2c 4 x 10c 1 x 20c 
Combination 3025: 10 x 1c 15 x 2c 2 x 10c 2 x 20c 
Combination 3026: 10 x 1c 15 x 2c 1 x 10c 1 x 50c 
Combination 3027: 10 x 1c 15 x 2c 3 x 20c 
Combination 3028: 10 x 1c 10 x 2c 14 x 5c 
Combination 3029: 10 x 1c 10 x 2c 12 x 5c 1 x 10c 
Combination 3030: 10 x 1c 10 x 2c 10 x 5c 2 x 10c 
Combination 3031: 10 x 1c 10 x 2c 10 x 5c 1 x 20c 
Combination 3032: 10 x 1c 10 x 2c 8 x 5c 3 x 10c 
Combination 3033: 10 x 1c 10 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 3034: 10 x 1c 10 x 2c 6 x 5c 4 x 10c 
Combination 3035: 10 x 1c 10 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 3036: 10 x 1c 10 x 2c 6 x 5c 2 x 20c 
Combination 3037: 10 x 1c 10 x 2c 4 x 5c 5 x 10c 
Combination 3038: 10 x 1c 10 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 3039: 10 x 1c 10 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 3040: 10 x 1c 10 x 2c 4 x 5c 1 x 50c 
Combination 3041: 10 x 1c 10 x 2c 2 x 5c 6 x 10c 
Combination 3042: 10 x 1c 10 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 3043: 10 x 1c 10 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 3044: 10 x 1c 10 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 3045: 10 x 1c 10 x 2c 2 x 5c 3 x 20c 
Combination 3046: 10 x 1c 10 x 2c 7 x 10c 
Combination 3047: 10 x 1c 10 x 2c 5 x 10c 1 x 20c 
Combination 3048: 10 x 1c 10 x 2c 3 x 10c 2 x 20c 
Combination 3049: 10 x 1c 10 x 2c 2 x 10c 1 x 50c 
Combination 3050: 10 x 1c 10 x 2c 1 x 10c 3 x 20c 
Combination 3051: 10 x 1c 10 x 2c 1 x 20c 1 x 50c 
Combination 3052: 10 x 1c 5 x 2c 16 x 5c 
Combination 3053: 10 x 1c 5 x 2c 14 x 5c 1 x 10c 
Combination 3054: 10 x 1c 5 x 2c 12 x 5c 2 x 10c 
Combination 3055: 10 x 1c 5 x 2c 12 x 5c 1 x 20c 
Combination 3056: 10 x 1c 5 x 2c 10 x 5c 3 x 10c 
Combination 3057: 10 x 1c 5 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 3058: 10 x 1c 5 x 2c 8 x 5c 4 x 10c 
Combination 3059: 10 x 1c 5 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 3060: 10 x 1c 5 x 2c 8 x 5c 2 x 20c 
Combination 3061: 10 x 1c 5 x 2c 6 x 5c 5 x 10c 
Combination 3062: 10 x 1c 5 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 3063: 10 x 1c 5 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 3064: 10 x 1c 5 x 2c 6 x 5c 1 x 50c 
Combination 3065: 10 x 1c 5 x 2c 4 x 5c 6 x 10c 
Combination 3066: 10 x 1c 5 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 3067: 10 x 1c 5 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 3068: 10 x 1c 5 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 3069: 10 x 1c 5 x 2c 4 x 5c 3 x 20c 
Combination 3070: 10 x 1c 5 x 2c 2 x 5c 7 x 10c 
Combination 3071: 10 x 1c 5 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 3072: 10 x 1c 5 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 3073: 10 x 1c 5 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 3074: 10 x 1c 5 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 3075: 10 x 1c 5 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 3076: 10 x 1c 5 x 2c 8 x 10c 
Combination 3077: 10 x 1c 5 x 2c 6 x 10c 1 x 20c 
Combination 3078: 10 x 1c 5 x 2c 4 x 10c 2 x 20c 
Combination 3079: 10 x 1c 5 x 2c 3 x 10c 1 x 50c 
Combination 3080: 10 x 1c 5 x 2c 2 x 10c 3 x 20c 
Combination 3081: 10 x 1c 5 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 3082: 10 x 1c 5 x 2c 4 x 20c 
Combination 3083: 10 x 1c 18 x 5c 
Combination 3084: 10 x 1c 16 x 5c 1 x 10c 
Combination 3085: 10 x 1c 14 x 5c 2 x 10c 
Combination 3086: 10 x 1c 14 x 5c 1 x 20c 
Combination 3087: 10 x 1c 12 x 5c 3 x 10c 
Combination 3088: 10 x 1c 12 x 5c 1 x 10c 1 x 20c 
Combination 3089: 10 x 1c 10 x 5c 4 x 10c 
Combination 3090: 10 x 1c 10 x 5c 2 x 10c 1 x 20c 
Combination 3091: 10 x 1c 10 x 5c 2 x 20c 
Combination 3092: 10 x 1c 8 x 5c 5 x 10c 
Combination 3093: 10 x 1c 8 x 5c 3 x 10c 1 x 20c 
Combination 3094: 10 x 1c 8 x 5c 1 x 10c 2 x 20c 
Combination 3095: 10 x 1c 8 x 5c 1 x 50c 
Combination 3096: 10 x 1c 6 x 5c 6 x 10c 
Combination 3097: 10 x 1c 6 x 5c 4 x 10c 1 x 20c 
Combination 3098: 10 x 1c 6 x 5c 2 x 10c 2 x 20c 
Combination 3099: 10 x 1c 6 x 5c 1 x 10c 1 x 50c 
Combination 3100: 10 x 1c 6 x 5c 3 x 20c 
Combination 3101: 10 x 1c 4 x 5c 7 x 10c 
Combination 3102: 10 x 1c 4 x 5c 5 x 10c 1 x 20c 
Combination 3103: 10 x 1c 4 x 5c 3 x 10c 2 x 20c 
Combination 3104: 10 x 1c 4 x 5c 2 x 10c 1 x 50c 
Combination 3105: 10 x 1c 4 x 5c 1 x 10c 3 x 20c 
Combination 3106: 10 x 1c 4 x 5c 1 x 20c 1 x 50c 
Combination 3107: 10 x 1c 2 x 5c 8 x 10c 
Combination 3108: 10 x 1c 2 x 5c 6 x 10c 1 x 20c 
Combination 3109: 10 x 1c 2 x 5c 4 x 10c 2 x 20c 
Combination 3110: 10 x 1c 2 x 5c 3 x 10c 1 x 50c 
Combination 3111: 10 x 1c 2 x 5c 2 x 10c 3 x 20c 
Combination 3112: 10 x 1c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3113: 10 x 1c 2 x 5c 4 x 20c 
Combination 3114: 10 x 1c 9 x 10c 
Combination 3115: 10 x 1c 7 x 10c 1 x 20c 
Combination 3116: 10 x 1c 5 x 10c 2 x 20c 
Combination 3117: 10 x 1c 4 x 10c 1 x 50c 
Combination 3118: 10 x 1c 3 x 10c 3 x 20c 
Combination 3119: 10 x 1c 2 x 10c 1 x 20c 1 x 50c 
Combination 3120: 10 x 1c 1 x 10c 4 x 20c 
Combination 3121: 10 x 1c 2 x 20c 1 x 50c 
Combination 3122: 9 x 1c 43 x 2c 1 x 5c 
Combination 3123: 9 x 1c 38 x 2c 3 x 5c 
Combination 3124: 9 x 1c 38 x 2c 1 x 5c 1 x 10c 
Combination 3125: 9 x 1c 33 x 2c 5 x 5c 
Combination 3126: 9 x 1c 33 x 2c 3 x 5c 1 x 10c 
Combination 3127: 9 x 1c 33 x 2c 1 x 5c 2 x 10c 
Combination 3128: 9 x 1c 33 x 2c 1 x 5c 1 x 20c 
Combination 3129: 9 x 1c 28 x 2c 7 x 5c 
Combination 3130: 9 x 1c 28 x 2c 5 x 5c 1 x 10c 
Combination 3131: 9 x 1c 28 x 2c 3 x 5c 2 x 10c 
Combination 3132: 9 x 1c 28 x 2c 3 x 5c 1 x 20c 
Combination 3133: 9 x 1c 28 x 2c 1 x 5c 3 x 10c 
Combination 3134: 9 x 1c 28 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 3135: 9 x 1c 23 x 2c 9 x 5c 
Combination 3136: 9 x 1c 23 x 2c 7 x 5c 1 x 10c 
Combination 3137: 9 x 1c 23 x 2c 5 x 5c 2 x 10c 
Combination 3138: 9 x 1c 23 x 2c 5 x 5c 1 x 20c 
Combination 3139: 9 x 1c 23 x 2c 3 x 5c 3 x 10c 
Combination 3140: 9 x 1c 23 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 3141: 9 x 1c 23 x 2c 1 x 5c 4 x 10c 
Combination 3142: 9 x 1c 23 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 3143: 9 x 1c 23 x 2c 1 x 5c 2 x 20c 
Combination 3144: 9 x 1c 18 x 2c 11 x 5c 
Combination 3145: 9 x 1c 18 x 2c 9 x 5c 1 x 10c 
Combination 3146: 9 x 1c 18 x 2c 7 x 5c 2 x 10c 
Combination 3147: 9 x 1c 18 x 2c 7 x 5c 1 x 20c 
Combination 3148: 9 x 1c 18 x 2c 5 x 5c 3 x 10c 
Combination 3149: 9 x 1c 18 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 3150: 9 x 1c 18 x 2c 3 x 5c 4 x 10c 
Combination 3151: 9 x 1c 18 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 3152: 9 x 1c 18 x 2c 3 x 5c 2 x 20c 
Combination 3153: 9 x 1c 18 x 2c 1 x 5c 5 x 10c 
Combination 3154: 9 x 1c 18 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 3155: 9 x 1c 18 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 3156: 9 x 1c 18 x 2c 1 x 5c 1 x 50c 
Combination 3157: 9 x 1c 13 x 2c 13 x 5c 
Combination 3158: 9 x 1c 13 x 2c 11 x 5c 1 x 10c 
Combination 3159: 9 x 1c 13 x 2c 9 x 5c 2 x 10c 
Combination 3160: 9 x 1c 13 x 2c 9 x 5c 1 x 20c 
Combination 3161: 9 x 1c 13 x 2c 7 x 5c 3 x 10c 
Combination 3162: 9 x 1c 13 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 3163: 9 x 1c 13 x 2c 5 x 5c 4 x 10c 
Combination 3164: 9 x 1c 13 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 3165: 9 x 1c 13 x 2c 5 x 5c 2 x 20c 
Combination 3166: 9 x 1c 13 x 2c 3 x 5c 5 x 10c 
Combination 3167: 9 x 1c 13 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 3168: 9 x 1c 13 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 3169: 9 x 1c 13 x 2c 3 x 5c 1 x 50c 
Combination 3170: 9 x 1c 13 x 2c 1 x 5c 6 x 10c 
Combination 3171: 9 x 1c 13 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 3172: 9 x 1c 13 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 3173: 9 x 1c 13 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 3174: 9 x 1c 13 x 2c 1 x 5c 3 x 20c 
Combination 3175: 9 x 1c 8 x 2c 15 x 5c 
Combination 3176: 9 x 1c 8 x 2c 13 x 5c 1 x 10c 
Combination 3177: 9 x 1c 8 x 2c 11 x 5c 2 x 10c 
Combination 3178: 9 x 1c 8 x 2c 11 x 5c 1 x 20c 
Combination 3179: 9 x 1c 8 x 2c 9 x 5c 3 x 10c 
Combination 3180: 9 x 1c 8 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 3181: 9 x 1c 8 x 2c 7 x 5c 4 x 10c 
Combination 3182: 9 x 1c 8 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 3183: 9 x 1c 8 x 2c 7 x 5c 2 x 20c 
Combination 3184: 9 x 1c 8 x 2c 5 x 5c 5 x 10c 
Combination 3185: 9 x 1c 8 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 3186: 9 x 1c 8 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 3187: 9 x 1c 8 x 2c 5 x 5c 1 x 50c 
Combination 3188: 9 x 1c 8 x 2c 3 x 5c 6 x 10c 
Combination 3189: 9 x 1c 8 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 3190: 9 x 1c 8 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 3191: 9 x 1c 8 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 3192: 9 x 1c 8 x 2c 3 x 5c 3 x 20c 
Combination 3193: 9 x 1c 8 x 2c 1 x 5c 7 x 10c 
Combination 3194: 9 x 1c 8 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 3195: 9 x 1c 8 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 3196: 9 x 1c 8 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 3197: 9 x 1c 8 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 3198: 9 x 1c 8 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 3199: 9 x 1c 3 x 2c 17 x 5c 
Combination 3200: 9 x 1c 3 x 2c 15 x 5c 1 x 10c 
Combination 3201: 9 x 1c 3 x 2c 13 x 5c 2 x 10c 
Combination 3202: 9 x 1c 3 x 2c 13 x 5c 1 x 20c 
Combination 3203: 9 x 1c 3 x 2c 11 x 5c 3 x 10c 
Combination 3204: 9 x 1c 3 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 3205: 9 x 1c 3 x 2c 9 x 5c 4 x 10c 
Combination 3206: 9 x 1c 3 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 3207: 9 x 1c 3 x 2c 9 x 5c 2 x 20c 
Combination 3208: 9 x 1c 3 x 2c 7 x 5c 5 x 10c 
Combination 3209: 9 x 1c 3 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 3210: 9 x 1c 3 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 3211: 9 x 1c 3 x 2c 7 x 5c 1 x 50c 
Combination 3212: 9 x 1c 3 x 2c 5 x 5c 6 x 10c 
Combination 3213: 9 x 1c 3 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 3214: 9 x 1c 3 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 3215: 9 x 1c 3 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 3216: 9 x 1c 3 x 2c 5 x 5c 3 x 20c 
Combination 3217: 9 x 1c 3 x 2c 3 x 5c 7 x 10c 
Combination 3218: 9 x 1c 3 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 3219: 9 x 1c 3 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 3220: 9 x 1c 3 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 3221: 9 x 1c 3 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 3222: 9 x 1c 3 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 3223: 9 x 1c 3 x 2c 1 x 5c 8 x 10c 
Combination 3224: 9 x 1c 3 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 3225: 9 x 1c 3 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 3226: 9 x 1c 3 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 3227: 9 x 1c 3 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 3228: 9 x 1c 3 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3229: 9 x 1c 3 x 2c 1 x 5c 4 x 20c 
Combination 3230: 8 x 1c 46 x 2c 
Combination 3231: 8 x 1c 41 x 2c 2 x 5c 
Combination 3232: 8 x 1c 41 x 2c 1 x 10c 
Combination 3233: 8 x 1c 36 x 2c 4 x 5c 
Combination 3234: 8 x 1c 36 x 2c 2 x 5c 1 x 10c 
Combination 3235: 8 x 1c 36 x 2c 2 x 10c 
Combination 3236: 8 x 1c 36 x 2c 1 x 20c 
Combination 3237: 8 x 1c 31 x 2c 6 x 5c 
Combination 3238: 8 x 1c 31 x 2c 4 x 5c 1 x 10c 
Combination 3239: 8 x 1c 31 x 2c 2 x 5c 2 x 10c 
Combination 3240: 8 x 1c 31 x 2c 2 x 5c 1 x 20c 
Combination 3241: 8 x 1c 31 x 2c 3 x 10c 
Combination 3242: 8 x 1c 31 x 2c 1 x 10c 1 x 20c 
Combination 3243: 8 x 1c 26 x 2c 8 x 5c 
Combination 3244: 8 x 1c 26 x 2c 6 x 5c 1 x 10c 
Combination 3245: 8 x 1c 26 x 2c 4 x 5c 2 x 10c 
Combination 3246: 8 x 1c 26 x 2c 4 x 5c 1 x 20c 
Combination 3247: 8 x 1c 26 x 2c 2 x 5c 3 x 10c 
Combination 3248: 8 x 1c 26 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 3249: 8 x 1c 26 x 2c 4 x 10c 
Combination 3250: 8 x 1c 26 x 2c 2 x 10c 1 x 20c 
Combination 3251: 8 x 1c 26 x 2c 2 x 20c 
Combination 3252: 8 x 1c 21 x 2c 10 x 5c 
Combination 3253: 8 x 1c 21 x 2c 8 x 5c 1 x 10c 
Combination 3254: 8 x 1c 21 x 2c 6 x 5c 2 x 10c 
Combination 3255: 8 x 1c 21 x 2c 6 x 5c 1 x 20c 
Combination 3256: 8 x 1c 21 x 2c 4 x 5c 3 x 10c 
Combination 3257: 8 x 1c 21 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 3258: 8 x 1c 21 x 2c 2 x 5c 4 x 10c 
Combination 3259: 8 x 1c 21 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 3260: 8 x 1c 21 x 2c 2 x 5c 2 x 20c 
Combination 3261: 8 x 1c 21 x 2c 5 x 10c 
Combination 3262: 8 x 1c 21 x 2c 3 x 10c 1 x 20c 
Combination 3263: 8 x 1c 21 x 2c 1 x 10c 2 x 20c 
Combination 3264: 8 x 1c 21 x 2c 1 x 50c 
Combination 3265: 8 x 1c 16 x 2c 12 x 5c 
Combination 3266: 8 x 1c 16 x 2c 10 x 5c 1 x 10c 
Combination 3267: 8 x 1c 16 x 2c 8 x 5c 2 x 10c 
Combination 3268: 8 x 1c 16 x 2c 8 x 5c 1 x 20c 
Combination 3269: 8 x 1c 16 x 2c 6 x 5c 3 x 10c 
Combination 3270: 8 x 1c 16 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 3271: 8 x 1c 16 x 2c 4 x 5c 4 x 10c 
Combination 3272: 8 x 1c 16 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 3273: 8 x 1c 16 x 2c 4 x 5c 2 x 20c 
Combination 3274: 8 x 1c 16 x 2c 2 x 5c 5 x 10c 
Combination 3275: 8 x 1c 16 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 3276: 8 x 1c 16 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 3277: 8 x 1c 16 x 2c 2 x 5c 1 x 50c 
Combination 3278: 8 x 1c 16 x 2c 6 x 10c 
Combination 3279: 8 x 1c 16 x 2c 4 x 10c 1 x 20c 
Combination 3280: 8 x 1c 16 x 2c 2 x 10c 2 x 20c 
Combination 3281: 8 x 1c 16 x 2c 1 x 10c 1 x 50c 
Combination 3282: 8 x 1c 16 x 2c 3 x 20c 
Combination 3283: 8 x 1c 11 x 2c 14 x 5c 
Combination 3284: 8 x 1c 11 x 2c 12 x 5c 1 x 10c 
Combination 3285: 8 x 1c 11 x 2c 10 x 5c 2 x 10c 
Combination 3286: 8 x 1c 11 x 2c 10 x 5c 1 x 20c 
Combination 3287: 8 x 1c 11 x 2c 8 x 5c 3 x 10c 
Combination 3288: 8 x 1c 11 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 3289: 8 x 1c 11 x 2c 6 x 5c 4 x 10c 
Combination 3290: 8 x 1c 11 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 3291: 8 x 1c 11 x 2c 6 x 5c 2 x 20c 
Combination 3292: 8 x 1c 11 x 2c 4 x 5c 5 x 10c 
Combination 3293: 8 x 1c 11 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 3294: 8 x 1c 11 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 3295: 8 x 1c 11 x 2c 4 x 5c 1 x 50c 
Combination 3296: 8 x 1c 11 x 2c 2 x 5c 6 x 10c 
Combination 3297: 8 x 1c 11 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 3298: 8 x 1c 11 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 3299: 8 x 1c 11 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 3300: 8 x 1c 11 x 2c 2 x 5c 3 x 20c 
Combination 3301: 8 x 1c 11 x 2c 7 x 10c 
Combination 3302: 8 x 1c 11 x 2c 5 x 10c 1 x 20c 
Combination 3303: 8 x 1c 11 x 2c 3 x 10c 2 x 20c 
Combination 3304: 8 x 1c 11 x 2c 2 x 10c 1 x 50c 
Combination 3305: 8 x 1c 11 x 2c 1 x 10c 3 x 20c 
Combination 3306: 8 x 1c 11 x 2c 1 x 20c 1 x 50c 
Combination 3307: 8 x 1c 6 x 2c 16 x 5c 
Combination 3308: 8 x 1c 6 x 2c 14 x 5c 1 x 10c 
Combination 3309: 8 x 1c 6 x 2c 12 x 5c 2 x 10c 
Combination 3310: 8 x 1c 6 x 2c 12 x 5c 1 x 20c 
Combination 3311: 8 x 1c 6 x 2c 10 x 5c 3 x 10c 
Combination 3312: 8 x 1c 6 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 3313: 8 x 1c 6 x 2c 8 x 5c 4 x 10c 
Combination 3314: 8 x 1c 6 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 3315: 8 x 1c 6 x 2c 8 x 5c 2 x 20c 
Combination 3316: 8 x 1c 6 x 2c 6 x 5c 5 x 10c 
Combination 3317: 8 x 1c 6 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 3318: 8 x 1c 6 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 3319: 8 x 1c 6 x 2c 6 x 5c 1 x 50c 
Combination 3320: 8 x 1c 6 x 2c 4 x 5c 6 x 10c 
Combination 3321: 8 x 1c 6 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 3322: 8 x 1c 6 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 3323: 8 x 1c 6 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 3324: 8 x 1c 6 x 2c 4 x 5c 3 x 20c 
Combination 3325: 8 x 1c 6 x 2c 2 x 5c 7 x 10c 
Combination 3326: 8 x 1c 6 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 3327: 8 x 1c 6 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 3328: 8 x 1c 6 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 3329: 8 x 1c 6 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 3330: 8 x 1c 6 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 3331: 8 x 1c 6 x 2c 8 x 10c 
Combination 3332: 8 x 1c 6 x 2c 6 x 10c 1 x 20c 
Combination 3333: 8 x 1c 6 x 2c 4 x 10c 2 x 20c 
Combination 3334: 8 x 1c 6 x 2c 3 x 10c 1 x 50c 
Combination 3335: 8 x 1c 6 x 2c 2 x 10c 3 x 20c 
Combination 3336: 8 x 1c 6 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 3337: 8 x 1c 6 x 2c 4 x 20c 
Combination 3338: 8 x 1c 1 x 2c 18 x 5c 
Combination 3339: 8 x 1c 1 x 2c 16 x 5c 1 x 10c 
Combination 3340: 8 x 1c 1 x 2c 14 x 5c 2 x 10c 
Combination 3341: 8 x 1c 1 x 2c 14 x 5c 1 x 20c 
Combination 3342: 8 x 1c 1 x 2c 12 x 5c 3 x 10c 
Combination 3343: 8 x 1c 1 x 2c 12 x 5c 1 x 10c 1 x 20c 
Combination 3344: 8 x 1c 1 x 2c 10 x 5c 4 x 10c 
Combination 3345: 8 x 1c 1 x 2c 10 x 5c 2 x 10c 1 x 20c 
Combination 3346: 8 x 1c 1 x 2c 10 x 5c 2 x 20c 
Combination 3347: 8 x 1c 1 x 2c 8 x 5c 5 x 10c 
Combination 3348: 8 x 1c 1 x 2c 8 x 5c 3 x 10c 1 x 20c 
Combination 3349: 8 x 1c 1 x 2c 8 x 5c 1 x 10c 2 x 20c 
Combination 3350: 8 x 1c 1 x 2c 8 x 5c 1 x 50c 
Combination 3351: 8 x 1c 1 x 2c 6 x 5c 6 x 10c 
Combination 3352: 8 x 1c 1 x 2c 6 x 5c 4 x 10c 1 x 20c 
Combination 3353: 8 x 1c 1 x 2c 6 x 5c 2 x 10c 2 x 20c 
Combination 3354: 8 x 1c 1 x 2c 6 x 5c 1 x 10c 1 x 50c 
Combination 3355: 8 x 1c 1 x 2c 6 x 5c 3 x 20c 
Combination 3356: 8 x 1c 1 x 2c 4 x 5c 7 x 10c 
Combination 3357: 8 x 1c 1 x 2c 4 x 5c 5 x 10c 1 x 20c 
Combination 3358: 8 x 1c 1 x 2c 4 x 5c 3 x 10c 2 x 20c 
Combination 3359: 8 x 1c 1 x 2c 4 x 5c 2 x 10c 1 x 50c 
Combination 3360: 8 x 1c 1 x 2c 4 x 5c 1 x 10c 3 x 20c 
Combination 3361: 8 x 1c 1 x 2c 4 x 5c 1 x 20c 1 x 50c 
Combination 3362: 8 x 1c 1 x 2c 2 x 5c 8 x 10c 
Combination 3363: 8 x 1c 1 x 2c 2 x 5c 6 x 10c 1 x 20c 
Combination 3364: 8 x 1c 1 x 2c 2 x 5c 4 x 10c 2 x 20c 
Combination 3365: 8 x 1c 1 x 2c 2 x 5c 3 x 10c 1 x 50c 
Combination 3366: 8 x 1c 1 x 2c 2 x 5c 2 x 10c 3 x 20c 
Combination 3367: 8 x 1c 1 x 2c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3368: 8 x 1c 1 x 2c 2 x 5c 4 x 20c 
Combination 3369: 8 x 1c 1 x 2c 9 x 10c 
Combination 3370: 8 x 1c 1 x 2c 7 x 10c 1 x 20c 
Combination 3371: 8 x 1c 1 x 2c 5 x 10c 2 x 20c 
Combination 3372: 8 x 1c 1 x 2c 4 x 10c 1 x 50c 
Combination 3373: 8 x 1c 1 x 2c 3 x 10c 3 x 20c 
Combination 3374: 8 x 1c 1 x 2c 2 x 10c 1 x 20c 1 x 50c 
Combination 3375: 8 x 1c 1 x 2c 1 x 10c 4 x 20c 
Combination 3376: 8 x 1c 1 x 2c 2 x 20c 1 x 50c 
Combination 3377: 7 x 1c 44 x 2c 1 x 5c 
Combination 3378: 7 x 1c 39 x 2c 3 x 5c 
Combination 3379: 7 x 1c 39 x 2c 1 x 5c 1 x 10c 
Combination 3380: 7 x 1c 34 x 2c 5 x 5c 
Combination 3381: 7 x 1c 34 x 2c 3 x 5c 1 x 10c 
Combination 3382: 7 x 1c 34 x 2c 1 x 5c 2 x 10c 
Combination 3383: 7 x 1c 34 x 2c 1 x 5c 1 x 20c 
Combination 3384: 7 x 1c 29 x 2c 7 x 5c 
Combination 3385: 7 x 1c 29 x 2c 5 x 5c 1 x 10c 
Combination 3386: 7 x 1c 29 x 2c 3 x 5c 2 x 10c 
Combination 3387: 7 x 1c 29 x 2c 3 x 5c 1 x 20c 
Combination 3388: 7 x 1c 29 x 2c 1 x 5c 3 x 10c 
Combination 3389: 7 x 1c 29 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 3390: 7 x 1c 24 x 2c 9 x 5c 
Combination 3391: 7 x 1c 24 x 2c 7 x 5c 1 x 10c 
Combination 3392: 7 x 1c 24 x 2c 5 x 5c 2 x 10c 
Combination 3393: 7 x 1c 24 x 2c 5 x 5c 1 x 20c 
Combination 3394: 7 x 1c 24 x 2c 3 x 5c 3 x 10c 
Combination 3395: 7 x 1c 24 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 3396: 7 x 1c 24 x 2c 1 x 5c 4 x 10c 
Combination 3397: 7 x 1c 24 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 3398: 7 x 1c 24 x 2c 1 x 5c 2 x 20c 
Combination 3399: 7 x 1c 19 x 2c 11 x 5c 
Combination 3400: 7 x 1c 19 x 2c 9 x 5c 1 x 10c 
Combination 3401: 7 x 1c 19 x 2c 7 x 5c 2 x 10c 
Combination 3402: 7 x 1c 19 x 2c 7 x 5c 1 x 20c 
Combination 3403: 7 x 1c 19 x 2c 5 x 5c 3 x 10c 
Combination 3404: 7 x 1c 19 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 3405: 7 x 1c 19 x 2c 3 x 5c 4 x 10c 
Combination 3406: 7 x 1c 19 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 3407: 7 x 1c 19 x 2c 3 x 5c 2 x 20c 
Combination 3408: 7 x 1c 19 x 2c 1 x 5c 5 x 10c 
Combination 3409: 7 x 1c 19 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 3410: 7 x 1c 19 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 3411: 7 x 1c 19 x 2c 1 x 5c 1 x 50c 
Combination 3412: 7 x 1c 14 x 2c 13 x 5c 
Combination 3413: 7 x 1c 14 x 2c 11 x 5c 1 x 10c 
Combination 3414: 7 x 1c 14 x 2c 9 x 5c 2 x 10c 
Combination 3415: 7 x 1c 14 x 2c 9 x 5c 1 x 20c 
Combination 3416: 7 x 1c 14 x 2c 7 x 5c 3 x 10c 
Combination 3417: 7 x 1c 14 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 3418: 7 x 1c 14 x 2c 5 x 5c 4 x 10c 
Combination 3419: 7 x 1c 14 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 3420: 7 x 1c 14 x 2c 5 x 5c 2 x 20c 
Combination 3421: 7 x 1c 14 x 2c 3 x 5c 5 x 10c 
Combination 3422: 7 x 1c 14 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 3423: 7 x 1c 14 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 3424: 7 x 1c 14 x 2c 3 x 5c 1 x 50c 
Combination 3425: 7 x 1c 14 x 2c 1 x 5c 6 x 10c 
Combination 3426: 7 x 1c 14 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 3427: 7 x 1c 14 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 3428: 7 x 1c 14 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 3429: 7 x 1c 14 x 2c 1 x 5c 3 x 20c 
Combination 3430: 7 x 1c 9 x 2c 15 x 5c 
Combination 3431: 7 x 1c 9 x 2c 13 x 5c 1 x 10c 
Combination 3432: 7 x 1c 9 x 2c 11 x 5c 2 x 10c 
Combination 3433: 7 x 1c 9 x 2c 11 x 5c 1 x 20c 
Combination 3434: 7 x 1c 9 x 2c 9 x 5c 3 x 10c 
Combination 3435: 7 x 1c 9 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 3436: 7 x 1c 9 x 2c 7 x 5c 4 x 10c 
Combination 3437: 7 x 1c 9 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 3438: 7 x 1c 9 x 2c 7 x 5c 2 x 20c 
Combination 3439: 7 x 1c 9 x 2c 5 x 5c 5 x 10c 
Combination 3440: 7 x 1c 9 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 3441: 7 x 1c 9 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 3442: 7 x 1c 9 x 2c 5 x 5c 1 x 50c 
Combination 3443: 7 x 1c 9 x 2c 3 x 5c 6 x 10c 
Combination 3444: 7 x 1c 9 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 3445: 7 x 1c 9 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 3446: 7 x 1c 9 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 3447: 7 x 1c 9 x 2c 3 x 5c 3 x 20c 
Combination 3448: 7 x 1c 9 x 2c 1 x 5c 7 x 10c 
Combination 3449: 7 x 1c 9 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 3450: 7 x 1c 9 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 3451: 7 x 1c 9 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 3452: 7 x 1c 9 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 3453: 7 x 1c 9 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 3454: 7 x 1c 4 x 2c 17 x 5c 
Combination 3455: 7 x 1c 4 x 2c 15 x 5c 1 x 10c 
Combination 3456: 7 x 1c 4 x 2c 13 x 5c 2 x 10c 
Combination 3457: 7 x 1c 4 x 2c 13 x 5c 1 x 20c 
Combination 3458: 7 x 1c 4 x 2c 11 x 5c 3 x 10c 
Combination 3459: 7 x 1c 4 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 3460: 7 x 1c 4 x 2c 9 x 5c 4 x 10c 
Combination 3461: 7 x 1c 4 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 3462: 7 x 1c 4 x 2c 9 x 5c 2 x 20c 
Combination 3463: 7 x 1c 4 x 2c 7 x 5c 5 x 10c 
Combination 3464: 7 x 1c 4 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 3465: 7 x 1c 4 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 3466: 7 x 1c 4 x 2c 7 x 5c 1 x 50c 
Combination 3467: 7 x 1c 4 x 2c 5 x 5c 6 x 10c 
Combination 3468: 7 x 1c 4 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 3469: 7 x 1c 4 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 3470: 7 x 1c 4 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 3471: 7 x 1c 4 x 2c 5 x 5c 3 x 20c 
Combination 3472: 7 x 1c 4 x 2c 3 x 5c 7 x 10c 
Combination 3473: 7 x 1c 4 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 3474: 7 x 1c 4 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 3475: 7 x 1c 4 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 3476: 7 x 1c 4 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 3477: 7 x 1c 4 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 3478: 7 x 1c 4 x 2c 1 x 5c 8 x 10c 
Combination 3479: 7 x 1c 4 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 3480: 7 x 1c 4 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 3481: 7 x 1c 4 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 3482: 7 x 1c 4 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 3483: 7 x 1c 4 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3484: 7 x 1c 4 x 2c 1 x 5c 4 x 20c 
Combination 3485: 6 x 1c 47 x 2c 
Combination 3486: 6 x 1c 42 x 2c 2 x 5c 
Combination 3487: 6 x 1c 42 x 2c 1 x 10c 
Combination 3488: 6 x 1c 37 x 2c 4 x 5c 
Combination 3489: 6 x 1c 37 x 2c 2 x 5c 1 x 10c 
Combination 3490: 6 x 1c 37 x 2c 2 x 10c 
Combination 3491: 6 x 1c 37 x 2c 1 x 20c 
Combination 3492: 6 x 1c 32 x 2c 6 x 5c 
Combination 3493: 6 x 1c 32 x 2c 4 x 5c 1 x 10c 
Combination 3494: 6 x 1c 32 x 2c 2 x 5c 2 x 10c 
Combination 3495: 6 x 1c 32 x 2c 2 x 5c 1 x 20c 
Combination 3496: 6 x 1c 32 x 2c 3 x 10c 
Combination 3497: 6 x 1c 32 x 2c 1 x 10c 1 x 20c 
Combination 3498: 6 x 1c 27 x 2c 8 x 5c 
Combination 3499: 6 x 1c 27 x 2c 6 x 5c 1 x 10c 
Combination 3500: 6 x 1c 27 x 2c 4 x 5c 2 x 10c 
Combination 3501: 6 x 1c 27 x 2c 4 x 5c 1 x 20c 
Combination 3502: 6 x 1c 27 x 2c 2 x 5c 3 x 10c 
Combination 3503: 6 x 1c 27 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 3504: 6 x 1c 27 x 2c 4 x 10c 
Combination 3505: 6 x 1c 27 x 2c 2 x 10c 1 x 20c 
Combination 3506: 6 x 1c 27 x 2c 2 x 20c 
Combination 3507: 6 x 1c 22 x 2c 10 x 5c 
Combination 3508: 6 x 1c 22 x 2c 8 x 5c 1 x 10c 
Combination 3509: 6 x 1c 22 x 2c 6 x 5c 2 x 10c 
Combination 3510: 6 x 1c 22 x 2c 6 x 5c 1 x 20c 
Combination 3511: 6 x 1c 22 x 2c 4 x 5c 3 x 10c 
Combination 3512: 6 x 1c 22 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 3513: 6 x 1c 22 x 2c 2 x 5c 4 x 10c 
Combination 3514: 6 x 1c 22 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 3515: 6 x 1c 22 x 2c 2 x 5c 2 x 20c 
Combination 3516: 6 x 1c 22 x 2c 5 x 10c 
Combination 3517: 6 x 1c 22 x 2c 3 x 10c 1 x 20c 
Combination 3518: 6 x 1c 22 x 2c 1 x 10c 2 x 20c 
Combination 3519: 6 x 1c 22 x 2c 1 x 50c 
Combination 3520: 6 x 1c 17 x 2c 12 x 5c 
Combination 3521: 6 x 1c 17 x 2c 10 x 5c 1 x 10c 
Combination 3522: 6 x 1c 17 x 2c 8 x 5c 2 x 10c 
Combination 3523: 6 x 1c 17 x 2c 8 x 5c 1 x 20c 
Combination 3524: 6 x 1c 17 x 2c 6 x 5c 3 x 10c 
Combination 3525: 6 x 1c 17 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 3526: 6 x 1c 17 x 2c 4 x 5c 4 x 10c 
Combination 3527: 6 x 1c 17 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 3528: 6 x 1c 17 x 2c 4 x 5c 2 x 20c 
Combination 3529: 6 x 1c 17 x 2c 2 x 5c 5 x 10c 
Combination 3530: 6 x 1c 17 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 3531: 6 x 1c 17 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 3532: 6 x 1c 17 x 2c 2 x 5c 1 x 50c 
Combination 3533: 6 x 1c 17 x 2c 6 x 10c 
Combination 3534: 6 x 1c 17 x 2c 4 x 10c 1 x 20c 
Combination 3535: 6 x 1c 17 x 2c 2 x 10c 2 x 20c 
Combination 3536: 6 x 1c 17 x 2c 1 x 10c 1 x 50c 
Combination 3537: 6 x 1c 17 x 2c 3 x 20c 
Combination 3538: 6 x 1c 12 x 2c 14 x 5c 
Combination 3539: 6 x 1c 12 x 2c 12 x 5c 1 x 10c 
Combination 3540: 6 x 1c 12 x 2c 10 x 5c 2 x 10c 
Combination 3541: 6 x 1c 12 x 2c 10 x 5c 1 x 20c 
Combination 3542: 6 x 1c 12 x 2c 8 x 5c 3 x 10c 
Combination 3543: 6 x 1c 12 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 3544: 6 x 1c 12 x 2c 6 x 5c 4 x 10c 
Combination 3545: 6 x 1c 12 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 3546: 6 x 1c 12 x 2c 6 x 5c 2 x 20c 
Combination 3547: 6 x 1c 12 x 2c 4 x 5c 5 x 10c 
Combination 3548: 6 x 1c 12 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 3549: 6 x 1c 12 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 3550: 6 x 1c 12 x 2c 4 x 5c 1 x 50c 
Combination 3551: 6 x 1c 12 x 2c 2 x 5c 6 x 10c 
Combination 3552: 6 x 1c 12 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 3553: 6 x 1c 12 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 3554: 6 x 1c 12 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 3555: 6 x 1c 12 x 2c 2 x 5c 3 x 20c 
Combination 3556: 6 x 1c 12 x 2c 7 x 10c 
Combination 3557: 6 x 1c 12 x 2c 5 x 10c 1 x 20c 
Combination 3558: 6 x 1c 12 x 2c 3 x 10c 2 x 20c 
Combination 3559: 6 x 1c 12 x 2c 2 x 10c 1 x 50c 
Combination 3560: 6 x 1c 12 x 2c 1 x 10c 3 x 20c 
Combination 3561: 6 x 1c 12 x 2c 1 x 20c 1 x 50c 
Combination 3562: 6 x 1c 7 x 2c 16 x 5c 
Combination 3563: 6 x 1c 7 x 2c 14 x 5c 1 x 10c 
Combination 3564: 6 x 1c 7 x 2c 12 x 5c 2 x 10c 
Combination 3565: 6 x 1c 7 x 2c 12 x 5c 1 x 20c 
Combination 3566: 6 x 1c 7 x 2c 10 x 5c 3 x 10c 
Combination 3567: 6 x 1c 7 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 3568: 6 x 1c 7 x 2c 8 x 5c 4 x 10c 
Combination 3569: 6 x 1c 7 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 3570: 6 x 1c 7 x 2c 8 x 5c 2 x 20c 
Combination 3571: 6 x 1c 7 x 2c 6 x 5c 5 x 10c 
Combination 3572: 6 x 1c 7 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 3573: 6 x 1c 7 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 3574: 6 x 1c 7 x 2c 6 x 5c 1 x 50c 
Combination 3575: 6 x 1c 7 x 2c 4 x 5c 6 x 10c 
Combination 3576: 6 x 1c 7 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 3577: 6 x 1c 7 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 3578: 6 x 1c 7 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 3579: 6 x 1c 7 x 2c 4 x 5c 3 x 20c 
Combination 3580: 6 x 1c 7 x 2c 2 x 5c 7 x 10c 
Combination 3581: 6 x 1c 7 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 3582: 6 x 1c 7 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 3583: 6 x 1c 7 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 3584: 6 x 1c 7 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 3585: 6 x 1c 7 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 3586: 6 x 1c 7 x 2c 8 x 10c 
Combination 3587: 6 x 1c 7 x 2c 6 x 10c 1 x 20c 
Combination 3588: 6 x 1c 7 x 2c 4 x 10c 2 x 20c 
Combination 3589: 6 x 1c 7 x 2c 3 x 10c 1 x 50c 
Combination 3590: 6 x 1c 7 x 2c 2 x 10c 3 x 20c 
Combination 3591: 6 x 1c 7 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 3592: 6 x 1c 7 x 2c 4 x 20c 
Combination 3593: 6 x 1c 2 x 2c 18 x 5c 
Combination 3594: 6 x 1c 2 x 2c 16 x 5c 1 x 10c 
Combination 3595: 6 x 1c 2 x 2c 14 x 5c 2 x 10c 
Combination 3596: 6 x 1c 2 x 2c 14 x 5c 1 x 20c 
Combination 3597: 6 x 1c 2 x 2c 12 x 5c 3 x 10c 
Combination 3598: 6 x 1c 2 x 2c 12 x 5c 1 x 10c 1 x 20c 
Combination 3599: 6 x 1c 2 x 2c 10 x 5c 4 x 10c 
Combination 3600: 6 x 1c 2 x 2c 10 x 5c 2 x 10c 1 x 20c 
Combination 3601: 6 x 1c 2 x 2c 10 x 5c 2 x 20c 
Combination 3602: 6 x 1c 2 x 2c 8 x 5c 5 x 10c 
Combination 3603: 6 x 1c 2 x 2c 8 x 5c 3 x 10c 1 x 20c 
Combination 3604: 6 x 1c 2 x 2c 8 x 5c 1 x 10c 2 x 20c 
Combination 3605: 6 x 1c 2 x 2c 8 x 5c 1 x 50c 
Combination 3606: 6 x 1c 2 x 2c 6 x 5c 6 x 10c 
Combination 3607: 6 x 1c 2 x 2c 6 x 5c 4 x 10c 1 x 20c 
Combination 3608: 6 x 1c 2 x 2c 6 x 5c 2 x 10c 2 x 20c 
Combination 3609: 6 x 1c 2 x 2c 6 x 5c 1 x 10c 1 x 50c 
Combination 3610: 6 x 1c 2 x 2c 6 x 5c 3 x 20c 
Combination 3611: 6 x 1c 2 x 2c 4 x 5c 7 x 10c 
Combination 3612: 6 x 1c 2 x 2c 4 x 5c 5 x 10c 1 x 20c 
Combination 3613: 6 x 1c 2 x 2c 4 x 5c 3 x 10c 2 x 20c 
Combination 3614: 6 x 1c 2 x 2c 4 x 5c 2 x 10c 1 x 50c 
Combination 3615: 6 x 1c 2 x 2c 4 x 5c 1 x 10c 3 x 20c 
Combination 3616: 6 x 1c 2 x 2c 4 x 5c 1 x 20c 1 x 50c 
Combination 3617: 6 x 1c 2 x 2c 2 x 5c 8 x 10c 
Combination 3618: 6 x 1c 2 x 2c 2 x 5c 6 x 10c 1 x 20c 
Combination 3619: 6 x 1c 2 x 2c 2 x 5c 4 x 10c 2 x 20c 
Combination 3620: 6 x 1c 2 x 2c 2 x 5c 3 x 10c 1 x 50c 
Combination 3621: 6 x 1c 2 x 2c 2 x 5c 2 x 10c 3 x 20c 
Combination 3622: 6 x 1c 2 x 2c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3623: 6 x 1c 2 x 2c 2 x 5c 4 x 20c 
Combination 3624: 6 x 1c 2 x 2c 9 x 10c 
Combination 3625: 6 x 1c 2 x 2c 7 x 10c 1 x 20c 
Combination 3626: 6 x 1c 2 x 2c 5 x 10c 2 x 20c 
Combination 3627: 6 x 1c 2 x 2c 4 x 10c 1 x 50c 
Combination 3628: 6 x 1c 2 x 2c 3 x 10c 3 x 20c 
Combination 3629: 6 x 1c 2 x 2c 2 x 10c 1 x 20c 1 x 50c 
Combination 3630: 6 x 1c 2 x 2c 1 x 10c 4 x 20c 
Combination 3631: 6 x 1c 2 x 2c 2 x 20c 1 x 50c 
Combination 3632: 5 x 1c 45 x 2c 1 x 5c 
Combination 3633: 5 x 1c 40 x 2c 3 x 5c 
Combination 3634: 5 x 1c 40 x 2c 1 x 5c 1 x 10c 
Combination 3635: 5 x 1c 35 x 2c 5 x 5c 
Combination 3636: 5 x 1c 35 x 2c 3 x 5c 1 x 10c 
Combination 3637: 5 x 1c 35 x 2c 1 x 5c 2 x 10c 
Combination 3638: 5 x 1c 35 x 2c 1 x 5c 1 x 20c 
Combination 3639: 5 x 1c 30 x 2c 7 x 5c 
Combination 3640: 5 x 1c 30 x 2c 5 x 5c 1 x 10c 
Combination 3641: 5 x 1c 30 x 2c 3 x 5c 2 x 10c 
Combination 3642: 5 x 1c 30 x 2c 3 x 5c 1 x 20c 
Combination 3643: 5 x 1c 30 x 2c 1 x 5c 3 x 10c 
Combination 3644: 5 x 1c 30 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 3645: 5 x 1c 25 x 2c 9 x 5c 
Combination 3646: 5 x 1c 25 x 2c 7 x 5c 1 x 10c 
Combination 3647: 5 x 1c 25 x 2c 5 x 5c 2 x 10c 
Combination 3648: 5 x 1c 25 x 2c 5 x 5c 1 x 20c 
Combination 3649: 5 x 1c 25 x 2c 3 x 5c 3 x 10c 
Combination 3650: 5 x 1c 25 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 3651: 5 x 1c 25 x 2c 1 x 5c 4 x 10c 
Combination 3652: 5 x 1c 25 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 3653: 5 x 1c 25 x 2c 1 x 5c 2 x 20c 
Combination 3654: 5 x 1c 20 x 2c 11 x 5c 
Combination 3655: 5 x 1c 20 x 2c 9 x 5c 1 x 10c 
Combination 3656: 5 x 1c 20 x 2c 7 x 5c 2 x 10c 
Combination 3657: 5 x 1c 20 x 2c 7 x 5c 1 x 20c 
Combination 3658: 5 x 1c 20 x 2c 5 x 5c 3 x 10c 
Combination 3659: 5 x 1c 20 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 3660: 5 x 1c 20 x 2c 3 x 5c 4 x 10c 
Combination 3661: 5 x 1c 20 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 3662: 5 x 1c 20 x 2c 3 x 5c 2 x 20c 
Combination 3663: 5 x 1c 20 x 2c 1 x 5c 5 x 10c 
Combination 3664: 5 x 1c 20 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 3665: 5 x 1c 20 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 3666: 5 x 1c 20 x 2c 1 x 5c 1 x 50c 
Combination 3667: 5 x 1c 15 x 2c 13 x 5c 
Combination 3668: 5 x 1c 15 x 2c 11 x 5c 1 x 10c 
Combination 3669: 5 x 1c 15 x 2c 9 x 5c 2 x 10c 
Combination 3670: 5 x 1c 15 x 2c 9 x 5c 1 x 20c 
Combination 3671: 5 x 1c 15 x 2c 7 x 5c 3 x 10c 
Combination 3672: 5 x 1c 15 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 3673: 5 x 1c 15 x 2c 5 x 5c 4 x 10c 
Combination 3674: 5 x 1c 15 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 3675: 5 x 1c 15 x 2c 5 x 5c 2 x 20c 
Combination 3676: 5 x 1c 15 x 2c 3 x 5c 5 x 10c 
Combination 3677: 5 x 1c 15 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 3678: 5 x 1c 15 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 3679: 5 x 1c 15 x 2c 3 x 5c 1 x 50c 
Combination 3680: 5 x 1c 15 x 2c 1 x 5c 6 x 10c 
Combination 3681: 5 x 1c 15 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 3682: 5 x 1c 15 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 3683: 5 x 1c 15 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 3684: 5 x 1c 15 x 2c 1 x 5c 3 x 20c 
Combination 3685: 5 x 1c 10 x 2c 15 x 5c 
Combination 3686: 5 x 1c 10 x 2c 13 x 5c 1 x 10c 
Combination 3687: 5 x 1c 10 x 2c 11 x 5c 2 x 10c 
Combination 3688: 5 x 1c 10 x 2c 11 x 5c 1 x 20c 
Combination 3689: 5 x 1c 10 x 2c 9 x 5c 3 x 10c 
Combination 3690: 5 x 1c 10 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 3691: 5 x 1c 10 x 2c 7 x 5c 4 x 10c 
Combination 3692: 5 x 1c 10 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 3693: 5 x 1c 10 x 2c 7 x 5c 2 x 20c 
Combination 3694: 5 x 1c 10 x 2c 5 x 5c 5 x 10c 
Combination 3695: 5 x 1c 10 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 3696: 5 x 1c 10 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 3697: 5 x 1c 10 x 2c 5 x 5c 1 x 50c 
Combination 3698: 5 x 1c 10 x 2c 3 x 5c 6 x 10c 
Combination 3699: 5 x 1c 10 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 3700: 5 x 1c 10 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 3701: 5 x 1c 10 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 3702: 5 x 1c 10 x 2c 3 x 5c 3 x 20c 
Combination 3703: 5 x 1c 10 x 2c 1 x 5c 7 x 10c 
Combination 3704: 5 x 1c 10 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 3705: 5 x 1c 10 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 3706: 5 x 1c 10 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 3707: 5 x 1c 10 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 3708: 5 x 1c 10 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 3709: 5 x 1c 5 x 2c 17 x 5c 
Combination 3710: 5 x 1c 5 x 2c 15 x 5c 1 x 10c 
Combination 3711: 5 x 1c 5 x 2c 13 x 5c 2 x 10c 
Combination 3712: 5 x 1c 5 x 2c 13 x 5c 1 x 20c 
Combination 3713: 5 x 1c 5 x 2c 11 x 5c 3 x 10c 
Combination 3714: 5 x 1c 5 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 3715: 5 x 1c 5 x 2c 9 x 5c 4 x 10c 
Combination 3716: 5 x 1c 5 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 3717: 5 x 1c 5 x 2c 9 x 5c 2 x 20c 
Combination 3718: 5 x 1c 5 x 2c 7 x 5c 5 x 10c 
Combination 3719: 5 x 1c 5 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 3720: 5 x 1c 5 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 3721: 5 x 1c 5 x 2c 7 x 5c 1 x 50c 
Combination 3722: 5 x 1c 5 x 2c 5 x 5c 6 x 10c 
Combination 3723: 5 x 1c 5 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 3724: 5 x 1c 5 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 3725: 5 x 1c 5 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 3726: 5 x 1c 5 x 2c 5 x 5c 3 x 20c 
Combination 3727: 5 x 1c 5 x 2c 3 x 5c 7 x 10c 
Combination 3728: 5 x 1c 5 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 3729: 5 x 1c 5 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 3730: 5 x 1c 5 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 3731: 5 x 1c 5 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 3732: 5 x 1c 5 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 3733: 5 x 1c 5 x 2c 1 x 5c 8 x 10c 
Combination 3734: 5 x 1c 5 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 3735: 5 x 1c 5 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 3736: 5 x 1c 5 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 3737: 5 x 1c 5 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 3738: 5 x 1c 5 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3739: 5 x 1c 5 x 2c 1 x 5c 4 x 20c 
Combination 3740: 5 x 1c 19 x 5c 
Combination 3741: 5 x 1c 17 x 5c 1 x 10c 
Combination 3742: 5 x 1c 15 x 5c 2 x 10c 
Combination 3743: 5 x 1c 15 x 5c 1 x 20c 
Combination 3744: 5 x 1c 13 x 5c 3 x 10c 
Combination 3745: 5 x 1c 13 x 5c 1 x 10c 1 x 20c 
Combination 3746: 5 x 1c 11 x 5c 4 x 10c 
Combination 3747: 5 x 1c 11 x 5c 2 x 10c 1 x 20c 
Combination 3748: 5 x 1c 11 x 5c 2 x 20c 
Combination 3749: 5 x 1c 9 x 5c 5 x 10c 
Combination 3750: 5 x 1c 9 x 5c 3 x 10c 1 x 20c 
Combination 3751: 5 x 1c 9 x 5c 1 x 10c 2 x 20c 
Combination 3752: 5 x 1c 9 x 5c 1 x 50c 
Combination 3753: 5 x 1c 7 x 5c 6 x 10c 
Combination 3754: 5 x 1c 7 x 5c 4 x 10c 1 x 20c 
Combination 3755: 5 x 1c 7 x 5c 2 x 10c 2 x 20c 
Combination 3756: 5 x 1c 7 x 5c 1 x 10c 1 x 50c 
Combination 3757: 5 x 1c 7 x 5c 3 x 20c 
Combination 3758: 5 x 1c 5 x 5c 7 x 10c 
Combination 3759: 5 x 1c 5 x 5c 5 x 10c 1 x 20c 
Combination 3760: 5 x 1c 5 x 5c 3 x 10c 2 x 20c 
Combination 3761: 5 x 1c 5 x 5c 2 x 10c 1 x 50c 
Combination 3762: 5 x 1c 5 x 5c 1 x 10c 3 x 20c 
Combination 3763: 5 x 1c 5 x 5c 1 x 20c 1 x 50c 
Combination 3764: 5 x 1c 3 x 5c 8 x 10c 
Combination 3765: 5 x 1c 3 x 5c 6 x 10c 1 x 20c 
Combination 3766: 5 x 1c 3 x 5c 4 x 10c 2 x 20c 
Combination 3767: 5 x 1c 3 x 5c 3 x 10c 1 x 50c 
Combination 3768: 5 x 1c 3 x 5c 2 x 10c 3 x 20c 
Combination 3769: 5 x 1c 3 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3770: 5 x 1c 3 x 5c 4 x 20c 
Combination 3771: 5 x 1c 1 x 5c 9 x 10c 
Combination 3772: 5 x 1c 1 x 5c 7 x 10c 1 x 20c 
Combination 3773: 5 x 1c 1 x 5c 5 x 10c 2 x 20c 
Combination 3774: 5 x 1c 1 x 5c 4 x 10c 1 x 50c 
Combination 3775: 5 x 1c 1 x 5c 3 x 10c 3 x 20c 
Combination 3776: 5 x 1c 1 x 5c 2 x 10c 1 x 20c 1 x 50c 
Combination 3777: 5 x 1c 1 x 5c 1 x 10c 4 x 20c 
Combination 3778: 5 x 1c 1 x 5c 2 x 20c 1 x 50c 
Combination 3779: 4 x 1c 48 x 2c 
Combination 3780: 4 x 1c 43 x 2c 2 x 5c 
Combination 3781: 4 x 1c 43 x 2c 1 x 10c 
Combination 3782: 4 x 1c 38 x 2c 4 x 5c 
Combination 3783: 4 x 1c 38 x 2c 2 x 5c 1 x 10c 
Combination 3784: 4 x 1c 38 x 2c 2 x 10c 
Combination 3785: 4 x 1c 38 x 2c 1 x 20c 
Combination 3786: 4 x 1c 33 x 2c 6 x 5c 
Combination 3787: 4 x 1c 33 x 2c 4 x 5c 1 x 10c 
Combination 3788: 4 x 1c 33 x 2c 2 x 5c 2 x 10c 
Combination 3789: 4 x 1c 33 x 2c 2 x 5c 1 x 20c 
Combination 3790: 4 x 1c 33 x 2c 3 x 10c 
Combination 3791: 4 x 1c 33 x 2c 1 x 10c 1 x 20c 
Combination 3792: 4 x 1c 28 x 2c 8 x 5c 
Combination 3793: 4 x 1c 28 x 2c 6 x 5c 1 x 10c 
Combination 3794: 4 x 1c 28 x 2c 4 x 5c 2 x 10c 
Combination 3795: 4 x 1c 28 x 2c 4 x 5c 1 x 20c 
Combination 3796: 4 x 1c 28 x 2c 2 x 5c 3 x 10c 
Combination 3797: 4 x 1c 28 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 3798: 4 x 1c 28 x 2c 4 x 10c 
Combination 3799: 4 x 1c 28 x 2c 2 x 10c 1 x 20c 
Combination 3800: 4 x 1c 28 x 2c 2 x 20c 
Combination 3801: 4 x 1c 23 x 2c 10 x 5c 
Combination 3802: 4 x 1c 23 x 2c 8 x 5c 1 x 10c 
Combination 3803: 4 x 1c 23 x 2c 6 x 5c 2 x 10c 
Combination 3804: 4 x 1c 23 x 2c 6 x 5c 1 x 20c 
Combination 3805: 4 x 1c 23 x 2c 4 x 5c 3 x 10c 
Combination 3806: 4 x 1c 23 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 3807: 4 x 1c 23 x 2c 2 x 5c 4 x 10c 
Combination 3808: 4 x 1c 23 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 3809: 4 x 1c 23 x 2c 2 x 5c 2 x 20c 
Combination 3810: 4 x 1c 23 x 2c 5 x 10c 
Combination 3811: 4 x 1c 23 x 2c 3 x 10c 1 x 20c 
Combination 3812: 4 x 1c 23 x 2c 1 x 10c 2 x 20c 
Combination 3813: 4 x 1c 23 x 2c 1 x 50c 
Combination 3814: 4 x 1c 18 x 2c 12 x 5c 
Combination 3815: 4 x 1c 18 x 2c 10 x 5c 1 x 10c 
Combination 3816: 4 x 1c 18 x 2c 8 x 5c 2 x 10c 
Combination 3817: 4 x 1c 18 x 2c 8 x 5c 1 x 20c 
Combination 3818: 4 x 1c 18 x 2c 6 x 5c 3 x 10c 
Combination 3819: 4 x 1c 18 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 3820: 4 x 1c 18 x 2c 4 x 5c 4 x 10c 
Combination 3821: 4 x 1c 18 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 3822: 4 x 1c 18 x 2c 4 x 5c 2 x 20c 
Combination 3823: 4 x 1c 18 x 2c 2 x 5c 5 x 10c 
Combination 3824: 4 x 1c 18 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 3825: 4 x 1c 18 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 3826: 4 x 1c 18 x 2c 2 x 5c 1 x 50c 
Combination 3827: 4 x 1c 18 x 2c 6 x 10c 
Combination 3828: 4 x 1c 18 x 2c 4 x 10c 1 x 20c 
Combination 3829: 4 x 1c 18 x 2c 2 x 10c 2 x 20c 
Combination 3830: 4 x 1c 18 x 2c 1 x 10c 1 x 50c 
Combination 3831: 4 x 1c 18 x 2c 3 x 20c 
Combination 3832: 4 x 1c 13 x 2c 14 x 5c 
Combination 3833: 4 x 1c 13 x 2c 12 x 5c 1 x 10c 
Combination 3834: 4 x 1c 13 x 2c 10 x 5c 2 x 10c 
Combination 3835: 4 x 1c 13 x 2c 10 x 5c 1 x 20c 
Combination 3836: 4 x 1c 13 x 2c 8 x 5c 3 x 10c 
Combination 3837: 4 x 1c 13 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 3838: 4 x 1c 13 x 2c 6 x 5c 4 x 10c 
Combination 3839: 4 x 1c 13 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 3840: 4 x 1c 13 x 2c 6 x 5c 2 x 20c 
Combination 3841: 4 x 1c 13 x 2c 4 x 5c 5 x 10c 
Combination 3842: 4 x 1c 13 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 3843: 4 x 1c 13 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 3844: 4 x 1c 13 x 2c 4 x 5c 1 x 50c 
Combination 3845: 4 x 1c 13 x 2c 2 x 5c 6 x 10c 
Combination 3846: 4 x 1c 13 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 3847: 4 x 1c 13 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 3848: 4 x 1c 13 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 3849: 4 x 1c 13 x 2c 2 x 5c 3 x 20c 
Combination 3850: 4 x 1c 13 x 2c 7 x 10c 
Combination 3851: 4 x 1c 13 x 2c 5 x 10c 1 x 20c 
Combination 3852: 4 x 1c 13 x 2c 3 x 10c 2 x 20c 
Combination 3853: 4 x 1c 13 x 2c 2 x 10c 1 x 50c 
Combination 3854: 4 x 1c 13 x 2c 1 x 10c 3 x 20c 
Combination 3855: 4 x 1c 13 x 2c 1 x 20c 1 x 50c 
Combination 3856: 4 x 1c 8 x 2c 16 x 5c 
Combination 3857: 4 x 1c 8 x 2c 14 x 5c 1 x 10c 
Combination 3858: 4 x 1c 8 x 2c 12 x 5c 2 x 10c 
Combination 3859: 4 x 1c 8 x 2c 12 x 5c 1 x 20c 
Combination 3860: 4 x 1c 8 x 2c 10 x 5c 3 x 10c 
Combination 3861: 4 x 1c 8 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 3862: 4 x 1c 8 x 2c 8 x 5c 4 x 10c 
Combination 3863: 4 x 1c 8 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 3864: 4 x 1c 8 x 2c 8 x 5c 2 x 20c 
Combination 3865: 4 x 1c 8 x 2c 6 x 5c 5 x 10c 
Combination 3866: 4 x 1c 8 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 3867: 4 x 1c 8 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 3868: 4 x 1c 8 x 2c 6 x 5c 1 x 50c 
Combination 3869: 4 x 1c 8 x 2c 4 x 5c 6 x 10c 
Combination 3870: 4 x 1c 8 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 3871: 4 x 1c 8 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 3872: 4 x 1c 8 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 3873: 4 x 1c 8 x 2c 4 x 5c 3 x 20c 
Combination 3874: 4 x 1c 8 x 2c 2 x 5c 7 x 10c 
Combination 3875: 4 x 1c 8 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 3876: 4 x 1c 8 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 3877: 4 x 1c 8 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 3878: 4 x 1c 8 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 3879: 4 x 1c 8 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 3880: 4 x 1c 8 x 2c 8 x 10c 
Combination 3881: 4 x 1c 8 x 2c 6 x 10c 1 x 20c 
Combination 3882: 4 x 1c 8 x 2c 4 x 10c 2 x 20c 
Combination 3883: 4 x 1c 8 x 2c 3 x 10c 1 x 50c 
Combination 3884: 4 x 1c 8 x 2c 2 x 10c 3 x 20c 
Combination 3885: 4 x 1c 8 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 3886: 4 x 1c 8 x 2c 4 x 20c 
Combination 3887: 4 x 1c 3 x 2c 18 x 5c 
Combination 3888: 4 x 1c 3 x 2c 16 x 5c 1 x 10c 
Combination 3889: 4 x 1c 3 x 2c 14 x 5c 2 x 10c 
Combination 3890: 4 x 1c 3 x 2c 14 x 5c 1 x 20c 
Combination 3891: 4 x 1c 3 x 2c 12 x 5c 3 x 10c 
Combination 3892: 4 x 1c 3 x 2c 12 x 5c 1 x 10c 1 x 20c 
Combination 3893: 4 x 1c 3 x 2c 10 x 5c 4 x 10c 
Combination 3894: 4 x 1c 3 x 2c 10 x 5c 2 x 10c 1 x 20c 
Combination 3895: 4 x 1c 3 x 2c 10 x 5c 2 x 20c 
Combination 3896: 4 x 1c 3 x 2c 8 x 5c 5 x 10c 
Combination 3897: 4 x 1c 3 x 2c 8 x 5c 3 x 10c 1 x 20c 
Combination 3898: 4 x 1c 3 x 2c 8 x 5c 1 x 10c 2 x 20c 
Combination 3899: 4 x 1c 3 x 2c 8 x 5c 1 x 50c 
Combination 3900: 4 x 1c 3 x 2c 6 x 5c 6 x 10c 
Combination 3901: 4 x 1c 3 x 2c 6 x 5c 4 x 10c 1 x 20c 
Combination 3902: 4 x 1c 3 x 2c 6 x 5c 2 x 10c 2 x 20c 
Combination 3903: 4 x 1c 3 x 2c 6 x 5c 1 x 10c 1 x 50c 
Combination 3904: 4 x 1c 3 x 2c 6 x 5c 3 x 20c 
Combination 3905: 4 x 1c 3 x 2c 4 x 5c 7 x 10c 
Combination 3906: 4 x 1c 3 x 2c 4 x 5c 5 x 10c 1 x 20c 
Combination 3907: 4 x 1c 3 x 2c 4 x 5c 3 x 10c 2 x 20c 
Combination 3908: 4 x 1c 3 x 2c 4 x 5c 2 x 10c 1 x 50c 
Combination 3909: 4 x 1c 3 x 2c 4 x 5c 1 x 10c 3 x 20c 
Combination 3910: 4 x 1c 3 x 2c 4 x 5c 1 x 20c 1 x 50c 
Combination 3911: 4 x 1c 3 x 2c 2 x 5c 8 x 10c 
Combination 3912: 4 x 1c 3 x 2c 2 x 5c 6 x 10c 1 x 20c 
Combination 3913: 4 x 1c 3 x 2c 2 x 5c 4 x 10c 2 x 20c 
Combination 3914: 4 x 1c 3 x 2c 2 x 5c 3 x 10c 1 x 50c 
Combination 3915: 4 x 1c 3 x 2c 2 x 5c 2 x 10c 3 x 20c 
Combination 3916: 4 x 1c 3 x 2c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 3917: 4 x 1c 3 x 2c 2 x 5c 4 x 20c 
Combination 3918: 4 x 1c 3 x 2c 9 x 10c 
Combination 3919: 4 x 1c 3 x 2c 7 x 10c 1 x 20c 
Combination 3920: 4 x 1c 3 x 2c 5 x 10c 2 x 20c 
Combination 3921: 4 x 1c 3 x 2c 4 x 10c 1 x 50c 
Combination 3922: 4 x 1c 3 x 2c 3 x 10c 3 x 20c 
Combination 3923: 4 x 1c 3 x 2c 2 x 10c 1 x 20c 1 x 50c 
Combination 3924: 4 x 1c 3 x 2c 1 x 10c 4 x 20c 
Combination 3925: 4 x 1c 3 x 2c 2 x 20c 1 x 50c 
Combination 3926: 3 x 1c 46 x 2c 1 x 5c 
Combination 3927: 3 x 1c 41 x 2c 3 x 5c 
Combination 3928: 3 x 1c 41 x 2c 1 x 5c 1 x 10c 
Combination 3929: 3 x 1c 36 x 2c 5 x 5c 
Combination 3930: 3 x 1c 36 x 2c 3 x 5c 1 x 10c 
Combination 3931: 3 x 1c 36 x 2c 1 x 5c 2 x 10c 
Combination 3932: 3 x 1c 36 x 2c 1 x 5c 1 x 20c 
Combination 3933: 3 x 1c 31 x 2c 7 x 5c 
Combination 3934: 3 x 1c 31 x 2c 5 x 5c 1 x 10c 
Combination 3935: 3 x 1c 31 x 2c 3 x 5c 2 x 10c 
Combination 3936: 3 x 1c 31 x 2c 3 x 5c 1 x 20c 
Combination 3937: 3 x 1c 31 x 2c 1 x 5c 3 x 10c 
Combination 3938: 3 x 1c 31 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 3939: 3 x 1c 26 x 2c 9 x 5c 
Combination 3940: 3 x 1c 26 x 2c 7 x 5c 1 x 10c 
Combination 3941: 3 x 1c 26 x 2c 5 x 5c 2 x 10c 
Combination 3942: 3 x 1c 26 x 2c 5 x 5c 1 x 20c 
Combination 3943: 3 x 1c 26 x 2c 3 x 5c 3 x 10c 
Combination 3944: 3 x 1c 26 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 3945: 3 x 1c 26 x 2c 1 x 5c 4 x 10c 
Combination 3946: 3 x 1c 26 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 3947: 3 x 1c 26 x 2c 1 x 5c 2 x 20c 
Combination 3948: 3 x 1c 21 x 2c 11 x 5c 
Combination 3949: 3 x 1c 21 x 2c 9 x 5c 1 x 10c 
Combination 3950: 3 x 1c 21 x 2c 7 x 5c 2 x 10c 
Combination 3951: 3 x 1c 21 x 2c 7 x 5c 1 x 20c 
Combination 3952: 3 x 1c 21 x 2c 5 x 5c 3 x 10c 
Combination 3953: 3 x 1c 21 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 3954: 3 x 1c 21 x 2c 3 x 5c 4 x 10c 
Combination 3955: 3 x 1c 21 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 3956: 3 x 1c 21 x 2c 3 x 5c 2 x 20c 
Combination 3957: 3 x 1c 21 x 2c 1 x 5c 5 x 10c 
Combination 3958: 3 x 1c 21 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 3959: 3 x 1c 21 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 3960: 3 x 1c 21 x 2c 1 x 5c 1 x 50c 
Combination 3961: 3 x 1c 16 x 2c 13 x 5c 
Combination 3962: 3 x 1c 16 x 2c 11 x 5c 1 x 10c 
Combination 3963: 3 x 1c 16 x 2c 9 x 5c 2 x 10c 
Combination 3964: 3 x 1c 16 x 2c 9 x 5c 1 x 20c 
Combination 3965: 3 x 1c 16 x 2c 7 x 5c 3 x 10c 
Combination 3966: 3 x 1c 16 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 3967: 3 x 1c 16 x 2c 5 x 5c 4 x 10c 
Combination 3968: 3 x 1c 16 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 3969: 3 x 1c 16 x 2c 5 x 5c 2 x 20c 
Combination 3970: 3 x 1c 16 x 2c 3 x 5c 5 x 10c 
Combination 3971: 3 x 1c 16 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 3972: 3 x 1c 16 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 3973: 3 x 1c 16 x 2c 3 x 5c 1 x 50c 
Combination 3974: 3 x 1c 16 x 2c 1 x 5c 6 x 10c 
Combination 3975: 3 x 1c 16 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 3976: 3 x 1c 16 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 3977: 3 x 1c 16 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 3978: 3 x 1c 16 x 2c 1 x 5c 3 x 20c 
Combination 3979: 3 x 1c 11 x 2c 15 x 5c 
Combination 3980: 3 x 1c 11 x 2c 13 x 5c 1 x 10c 
Combination 3981: 3 x 1c 11 x 2c 11 x 5c 2 x 10c 
Combination 3982: 3 x 1c 11 x 2c 11 x 5c 1 x 20c 
Combination 3983: 3 x 1c 11 x 2c 9 x 5c 3 x 10c 
Combination 3984: 3 x 1c 11 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 3985: 3 x 1c 11 x 2c 7 x 5c 4 x 10c 
Combination 3986: 3 x 1c 11 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 3987: 3 x 1c 11 x 2c 7 x 5c 2 x 20c 
Combination 3988: 3 x 1c 11 x 2c 5 x 5c 5 x 10c 
Combination 3989: 3 x 1c 11 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 3990: 3 x 1c 11 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 3991: 3 x 1c 11 x 2c 5 x 5c 1 x 50c 
Combination 3992: 3 x 1c 11 x 2c 3 x 5c 6 x 10c 
Combination 3993: 3 x 1c 11 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 3994: 3 x 1c 11 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 3995: 3 x 1c 11 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 3996: 3 x 1c 11 x 2c 3 x 5c 3 x 20c 
Combination 3997: 3 x 1c 11 x 2c 1 x 5c 7 x 10c 
Combination 3998: 3 x 1c 11 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 3999: 3 x 1c 11 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 4000: 3 x 1c 11 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 4001: 3 x 1c 11 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 4002: 3 x 1c 11 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 4003: 3 x 1c 6 x 2c 17 x 5c 
Combination 4004: 3 x 1c 6 x 2c 15 x 5c 1 x 10c 
Combination 4005: 3 x 1c 6 x 2c 13 x 5c 2 x 10c 
Combination 4006: 3 x 1c 6 x 2c 13 x 5c 1 x 20c 
Combination 4007: 3 x 1c 6 x 2c 11 x 5c 3 x 10c 
Combination 4008: 3 x 1c 6 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 4009: 3 x 1c 6 x 2c 9 x 5c 4 x 10c 
Combination 4010: 3 x 1c 6 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 4011: 3 x 1c 6 x 2c 9 x 5c 2 x 20c 
Combination 4012: 3 x 1c 6 x 2c 7 x 5c 5 x 10c 
Combination 4013: 3 x 1c 6 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 4014: 3 x 1c 6 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 4015: 3 x 1c 6 x 2c 7 x 5c 1 x 50c 
Combination 4016: 3 x 1c 6 x 2c 5 x 5c 6 x 10c 
Combination 4017: 3 x 1c 6 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 4018: 3 x 1c 6 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 4019: 3 x 1c 6 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 4020: 3 x 1c 6 x 2c 5 x 5c 3 x 20c 
Combination 4021: 3 x 1c 6 x 2c 3 x 5c 7 x 10c 
Combination 4022: 3 x 1c 6 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 4023: 3 x 1c 6 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 4024: 3 x 1c 6 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 4025: 3 x 1c 6 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 4026: 3 x 1c 6 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 4027: 3 x 1c 6 x 2c 1 x 5c 8 x 10c 
Combination 4028: 3 x 1c 6 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 4029: 3 x 1c 6 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 4030: 3 x 1c 6 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 4031: 3 x 1c 6 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 4032: 3 x 1c 6 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4033: 3 x 1c 6 x 2c 1 x 5c 4 x 20c 
Combination 4034: 3 x 1c 1 x 2c 19 x 5c 
Combination 4035: 3 x 1c 1 x 2c 17 x 5c 1 x 10c 
Combination 4036: 3 x 1c 1 x 2c 15 x 5c 2 x 10c 
Combination 4037: 3 x 1c 1 x 2c 15 x 5c 1 x 20c 
Combination 4038: 3 x 1c 1 x 2c 13 x 5c 3 x 10c 
Combination 4039: 3 x 1c 1 x 2c 13 x 5c 1 x 10c 1 x 20c 
Combination 4040: 3 x 1c 1 x 2c 11 x 5c 4 x 10c 
Combination 4041: 3 x 1c 1 x 2c 11 x 5c 2 x 10c 1 x 20c 
Combination 4042: 3 x 1c 1 x 2c 11 x 5c 2 x 20c 
Combination 4043: 3 x 1c 1 x 2c 9 x 5c 5 x 10c 
Combination 4044: 3 x 1c 1 x 2c 9 x 5c 3 x 10c 1 x 20c 
Combination 4045: 3 x 1c 1 x 2c 9 x 5c 1 x 10c 2 x 20c 
Combination 4046: 3 x 1c 1 x 2c 9 x 5c 1 x 50c 
Combination 4047: 3 x 1c 1 x 2c 7 x 5c 6 x 10c 
Combination 4048: 3 x 1c 1 x 2c 7 x 5c 4 x 10c 1 x 20c 
Combination 4049: 3 x 1c 1 x 2c 7 x 5c 2 x 10c 2 x 20c 
Combination 4050: 3 x 1c 1 x 2c 7 x 5c 1 x 10c 1 x 50c 
Combination 4051: 3 x 1c 1 x 2c 7 x 5c 3 x 20c 
Combination 4052: 3 x 1c 1 x 2c 5 x 5c 7 x 10c 
Combination 4053: 3 x 1c 1 x 2c 5 x 5c 5 x 10c 1 x 20c 
Combination 4054: 3 x 1c 1 x 2c 5 x 5c 3 x 10c 2 x 20c 
Combination 4055: 3 x 1c 1 x 2c 5 x 5c 2 x 10c 1 x 50c 
Combination 4056: 3 x 1c 1 x 2c 5 x 5c 1 x 10c 3 x 20c 
Combination 4057: 3 x 1c 1 x 2c 5 x 5c 1 x 20c 1 x 50c 
Combination 4058: 3 x 1c 1 x 2c 3 x 5c 8 x 10c 
Combination 4059: 3 x 1c 1 x 2c 3 x 5c 6 x 10c 1 x 20c 
Combination 4060: 3 x 1c 1 x 2c 3 x 5c 4 x 10c 2 x 20c 
Combination 4061: 3 x 1c 1 x 2c 3 x 5c 3 x 10c 1 x 50c 
Combination 4062: 3 x 1c 1 x 2c 3 x 5c 2 x 10c 3 x 20c 
Combination 4063: 3 x 1c 1 x 2c 3 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4064: 3 x 1c 1 x 2c 3 x 5c 4 x 20c 
Combination 4065: 3 x 1c 1 x 2c 1 x 5c 9 x 10c 
Combination 4066: 3 x 1c 1 x 2c 1 x 5c 7 x 10c 1 x 20c 
Combination 4067: 3 x 1c 1 x 2c 1 x 5c 5 x 10c 2 x 20c 
Combination 4068: 3 x 1c 1 x 2c 1 x 5c 4 x 10c 1 x 50c 
Combination 4069: 3 x 1c 1 x 2c 1 x 5c 3 x 10c 3 x 20c 
Combination 4070: 3 x 1c 1 x 2c 1 x 5c 2 x 10c 1 x 20c 1 x 50c 
Combination 4071: 3 x 1c 1 x 2c 1 x 5c 1 x 10c 4 x 20c 
Combination 4072: 3 x 1c 1 x 2c 1 x 5c 2 x 20c 1 x 50c 
Combination 4073: 2 x 1c 49 x 2c 
Combination 4074: 2 x 1c 44 x 2c 2 x 5c 
Combination 4075: 2 x 1c 44 x 2c 1 x 10c 
Combination 4076: 2 x 1c 39 x 2c 4 x 5c 
Combination 4077: 2 x 1c 39 x 2c 2 x 5c 1 x 10c 
Combination 4078: 2 x 1c 39 x 2c 2 x 10c 
Combination 4079: 2 x 1c 39 x 2c 1 x 20c 
Combination 4080: 2 x 1c 34 x 2c 6 x 5c 
Combination 4081: 2 x 1c 34 x 2c 4 x 5c 1 x 10c 
Combination 4082: 2 x 1c 34 x 2c 2 x 5c 2 x 10c 
Combination 4083: 2 x 1c 34 x 2c 2 x 5c 1 x 20c 
Combination 4084: 2 x 1c 34 x 2c 3 x 10c 
Combination 4085: 2 x 1c 34 x 2c 1 x 10c 1 x 20c 
Combination 4086: 2 x 1c 29 x 2c 8 x 5c 
Combination 4087: 2 x 1c 29 x 2c 6 x 5c 1 x 10c 
Combination 4088: 2 x 1c 29 x 2c 4 x 5c 2 x 10c 
Combination 4089: 2 x 1c 29 x 2c 4 x 5c 1 x 20c 
Combination 4090: 2 x 1c 29 x 2c 2 x 5c 3 x 10c 
Combination 4091: 2 x 1c 29 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 4092: 2 x 1c 29 x 2c 4 x 10c 
Combination 4093: 2 x 1c 29 x 2c 2 x 10c 1 x 20c 
Combination 4094: 2 x 1c 29 x 2c 2 x 20c 
Combination 4095: 2 x 1c 24 x 2c 10 x 5c 
Combination 4096: 2 x 1c 24 x 2c 8 x 5c 1 x 10c 
Combination 4097: 2 x 1c 24 x 2c 6 x 5c 2 x 10c 
Combination 4098: 2 x 1c 24 x 2c 6 x 5c 1 x 20c 
Combination 4099: 2 x 1c 24 x 2c 4 x 5c 3 x 10c 
Combination 4100: 2 x 1c 24 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 4101: 2 x 1c 24 x 2c 2 x 5c 4 x 10c 
Combination 4102: 2 x 1c 24 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 4103: 2 x 1c 24 x 2c 2 x 5c 2 x 20c 
Combination 4104: 2 x 1c 24 x 2c 5 x 10c 
Combination 4105: 2 x 1c 24 x 2c 3 x 10c 1 x 20c 
Combination 4106: 2 x 1c 24 x 2c 1 x 10c 2 x 20c 
Combination 4107: 2 x 1c 24 x 2c 1 x 50c 
Combination 4108: 2 x 1c 19 x 2c 12 x 5c 
Combination 4109: 2 x 1c 19 x 2c 10 x 5c 1 x 10c 
Combination 4110: 2 x 1c 19 x 2c 8 x 5c 2 x 10c 
Combination 4111: 2 x 1c 19 x 2c 8 x 5c 1 x 20c 
Combination 4112: 2 x 1c 19 x 2c 6 x 5c 3 x 10c 
Combination 4113: 2 x 1c 19 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 4114: 2 x 1c 19 x 2c 4 x 5c 4 x 10c 
Combination 4115: 2 x 1c 19 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 4116: 2 x 1c 19 x 2c 4 x 5c 2 x 20c 
Combination 4117: 2 x 1c 19 x 2c 2 x 5c 5 x 10c 
Combination 4118: 2 x 1c 19 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 4119: 2 x 1c 19 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 4120: 2 x 1c 19 x 2c 2 x 5c 1 x 50c 
Combination 4121: 2 x 1c 19 x 2c 6 x 10c 
Combination 4122: 2 x 1c 19 x 2c 4 x 10c 1 x 20c 
Combination 4123: 2 x 1c 19 x 2c 2 x 10c 2 x 20c 
Combination 4124: 2 x 1c 19 x 2c 1 x 10c 1 x 50c 
Combination 4125: 2 x 1c 19 x 2c 3 x 20c 
Combination 4126: 2 x 1c 14 x 2c 14 x 5c 
Combination 4127: 2 x 1c 14 x 2c 12 x 5c 1 x 10c 
Combination 4128: 2 x 1c 14 x 2c 10 x 5c 2 x 10c 
Combination 4129: 2 x 1c 14 x 2c 10 x 5c 1 x 20c 
Combination 4130: 2 x 1c 14 x 2c 8 x 5c 3 x 10c 
Combination 4131: 2 x 1c 14 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 4132: 2 x 1c 14 x 2c 6 x 5c 4 x 10c 
Combination 4133: 2 x 1c 14 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 4134: 2 x 1c 14 x 2c 6 x 5c 2 x 20c 
Combination 4135: 2 x 1c 14 x 2c 4 x 5c 5 x 10c 
Combination 4136: 2 x 1c 14 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 4137: 2 x 1c 14 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 4138: 2 x 1c 14 x 2c 4 x 5c 1 x 50c 
Combination 4139: 2 x 1c 14 x 2c 2 x 5c 6 x 10c 
Combination 4140: 2 x 1c 14 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 4141: 2 x 1c 14 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 4142: 2 x 1c 14 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 4143: 2 x 1c 14 x 2c 2 x 5c 3 x 20c 
Combination 4144: 2 x 1c 14 x 2c 7 x 10c 
Combination 4145: 2 x 1c 14 x 2c 5 x 10c 1 x 20c 
Combination 4146: 2 x 1c 14 x 2c 3 x 10c 2 x 20c 
Combination 4147: 2 x 1c 14 x 2c 2 x 10c 1 x 50c 
Combination 4148: 2 x 1c 14 x 2c 1 x 10c 3 x 20c 
Combination 4149: 2 x 1c 14 x 2c 1 x 20c 1 x 50c 
Combination 4150: 2 x 1c 9 x 2c 16 x 5c 
Combination 4151: 2 x 1c 9 x 2c 14 x 5c 1 x 10c 
Combination 4152: 2 x 1c 9 x 2c 12 x 5c 2 x 10c 
Combination 4153: 2 x 1c 9 x 2c 12 x 5c 1 x 20c 
Combination 4154: 2 x 1c 9 x 2c 10 x 5c 3 x 10c 
Combination 4155: 2 x 1c 9 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 4156: 2 x 1c 9 x 2c 8 x 5c 4 x 10c 
Combination 4157: 2 x 1c 9 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 4158: 2 x 1c 9 x 2c 8 x 5c 2 x 20c 
Combination 4159: 2 x 1c 9 x 2c 6 x 5c 5 x 10c 
Combination 4160: 2 x 1c 9 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 4161: 2 x 1c 9 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 4162: 2 x 1c 9 x 2c 6 x 5c 1 x 50c 
Combination 4163: 2 x 1c 9 x 2c 4 x 5c 6 x 10c 
Combination 4164: 2 x 1c 9 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 4165: 2 x 1c 9 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 4166: 2 x 1c 9 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 4167: 2 x 1c 9 x 2c 4 x 5c 3 x 20c 
Combination 4168: 2 x 1c 9 x 2c 2 x 5c 7 x 10c 
Combination 4169: 2 x 1c 9 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 4170: 2 x 1c 9 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 4171: 2 x 1c 9 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 4172: 2 x 1c 9 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 4173: 2 x 1c 9 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 4174: 2 x 1c 9 x 2c 8 x 10c 
Combination 4175: 2 x 1c 9 x 2c 6 x 10c 1 x 20c 
Combination 4176: 2 x 1c 9 x 2c 4 x 10c 2 x 20c 
Combination 4177: 2 x 1c 9 x 2c 3 x 10c 1 x 50c 
Combination 4178: 2 x 1c 9 x 2c 2 x 10c 3 x 20c 
Combination 4179: 2 x 1c 9 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 4180: 2 x 1c 9 x 2c 4 x 20c 
Combination 4181: 2 x 1c 4 x 2c 18 x 5c 
Combination 4182: 2 x 1c 4 x 2c 16 x 5c 1 x 10c 
Combination 4183: 2 x 1c 4 x 2c 14 x 5c 2 x 10c 
Combination 4184: 2 x 1c 4 x 2c 14 x 5c 1 x 20c 
Combination 4185: 2 x 1c 4 x 2c 12 x 5c 3 x 10c 
Combination 4186: 2 x 1c 4 x 2c 12 x 5c 1 x 10c 1 x 20c 
Combination 4187: 2 x 1c 4 x 2c 10 x 5c 4 x 10c 
Combination 4188: 2 x 1c 4 x 2c 10 x 5c 2 x 10c 1 x 20c 
Combination 4189: 2 x 1c 4 x 2c 10 x 5c 2 x 20c 
Combination 4190: 2 x 1c 4 x 2c 8 x 5c 5 x 10c 
Combination 4191: 2 x 1c 4 x 2c 8 x 5c 3 x 10c 1 x 20c 
Combination 4192: 2 x 1c 4 x 2c 8 x 5c 1 x 10c 2 x 20c 
Combination 4193: 2 x 1c 4 x 2c 8 x 5c 1 x 50c 
Combination 4194: 2 x 1c 4 x 2c 6 x 5c 6 x 10c 
Combination 4195: 2 x 1c 4 x 2c 6 x 5c 4 x 10c 1 x 20c 
Combination 4196: 2 x 1c 4 x 2c 6 x 5c 2 x 10c 2 x 20c 
Combination 4197: 2 x 1c 4 x 2c 6 x 5c 1 x 10c 1 x 50c 
Combination 4198: 2 x 1c 4 x 2c 6 x 5c 3 x 20c 
Combination 4199: 2 x 1c 4 x 2c 4 x 5c 7 x 10c 
Combination 4200: 2 x 1c 4 x 2c 4 x 5c 5 x 10c 1 x 20c 
Combination 4201: 2 x 1c 4 x 2c 4 x 5c 3 x 10c 2 x 20c 
Combination 4202: 2 x 1c 4 x 2c 4 x 5c 2 x 10c 1 x 50c 
Combination 4203: 2 x 1c 4 x 2c 4 x 5c 1 x 10c 3 x 20c 
Combination 4204: 2 x 1c 4 x 2c 4 x 5c 1 x 20c 1 x 50c 
Combination 4205: 2 x 1c 4 x 2c 2 x 5c 8 x 10c 
Combination 4206: 2 x 1c 4 x 2c 2 x 5c 6 x 10c 1 x 20c 
Combination 4207: 2 x 1c 4 x 2c 2 x 5c 4 x 10c 2 x 20c 
Combination 4208: 2 x 1c 4 x 2c 2 x 5c 3 x 10c 1 x 50c 
Combination 4209: 2 x 1c 4 x 2c 2 x 5c 2 x 10c 3 x 20c 
Combination 4210: 2 x 1c 4 x 2c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4211: 2 x 1c 4 x 2c 2 x 5c 4 x 20c 
Combination 4212: 2 x 1c 4 x 2c 9 x 10c 
Combination 4213: 2 x 1c 4 x 2c 7 x 10c 1 x 20c 
Combination 4214: 2 x 1c 4 x 2c 5 x 10c 2 x 20c 
Combination 4215: 2 x 1c 4 x 2c 4 x 10c 1 x 50c 
Combination 4216: 2 x 1c 4 x 2c 3 x 10c 3 x 20c 
Combination 4217: 2 x 1c 4 x 2c 2 x 10c 1 x 20c 1 x 50c 
Combination 4218: 2 x 1c 4 x 2c 1 x 10c 4 x 20c 
Combination 4219: 2 x 1c 4 x 2c 2 x 20c 1 x 50c 
Combination 4220: 1 x 1c 47 x 2c 1 x 5c 
Combination 4221: 1 x 1c 42 x 2c 3 x 5c 
Combination 4222: 1 x 1c 42 x 2c 1 x 5c 1 x 10c 
Combination 4223: 1 x 1c 37 x 2c 5 x 5c 
Combination 4224: 1 x 1c 37 x 2c 3 x 5c 1 x 10c 
Combination 4225: 1 x 1c 37 x 2c 1 x 5c 2 x 10c 
Combination 4226: 1 x 1c 37 x 2c 1 x 5c 1 x 20c 
Combination 4227: 1 x 1c 32 x 2c 7 x 5c 
Combination 4228: 1 x 1c 32 x 2c 5 x 5c 1 x 10c 
Combination 4229: 1 x 1c 32 x 2c 3 x 5c 2 x 10c 
Combination 4230: 1 x 1c 32 x 2c 3 x 5c 1 x 20c 
Combination 4231: 1 x 1c 32 x 2c 1 x 5c 3 x 10c 
Combination 4232: 1 x 1c 32 x 2c 1 x 5c 1 x 10c 1 x 20c 
Combination 4233: 1 x 1c 27 x 2c 9 x 5c 
Combination 4234: 1 x 1c 27 x 2c 7 x 5c 1 x 10c 
Combination 4235: 1 x 1c 27 x 2c 5 x 5c 2 x 10c 
Combination 4236: 1 x 1c 27 x 2c 5 x 5c 1 x 20c 
Combination 4237: 1 x 1c 27 x 2c 3 x 5c 3 x 10c 
Combination 4238: 1 x 1c 27 x 2c 3 x 5c 1 x 10c 1 x 20c 
Combination 4239: 1 x 1c 27 x 2c 1 x 5c 4 x 10c 
Combination 4240: 1 x 1c 27 x 2c 1 x 5c 2 x 10c 1 x 20c 
Combination 4241: 1 x 1c 27 x 2c 1 x 5c 2 x 20c 
Combination 4242: 1 x 1c 22 x 2c 11 x 5c 
Combination 4243: 1 x 1c 22 x 2c 9 x 5c 1 x 10c 
Combination 4244: 1 x 1c 22 x 2c 7 x 5c 2 x 10c 
Combination 4245: 1 x 1c 22 x 2c 7 x 5c 1 x 20c 
Combination 4246: 1 x 1c 22 x 2c 5 x 5c 3 x 10c 
Combination 4247: 1 x 1c 22 x 2c 5 x 5c 1 x 10c 1 x 20c 
Combination 4248: 1 x 1c 22 x 2c 3 x 5c 4 x 10c 
Combination 4249: 1 x 1c 22 x 2c 3 x 5c 2 x 10c 1 x 20c 
Combination 4250: 1 x 1c 22 x 2c 3 x 5c 2 x 20c 
Combination 4251: 1 x 1c 22 x 2c 1 x 5c 5 x 10c 
Combination 4252: 1 x 1c 22 x 2c 1 x 5c 3 x 10c 1 x 20c 
Combination 4253: 1 x 1c 22 x 2c 1 x 5c 1 x 10c 2 x 20c 
Combination 4254: 1 x 1c 22 x 2c 1 x 5c 1 x 50c 
Combination 4255: 1 x 1c 17 x 2c 13 x 5c 
Combination 4256: 1 x 1c 17 x 2c 11 x 5c 1 x 10c 
Combination 4257: 1 x 1c 17 x 2c 9 x 5c 2 x 10c 
Combination 4258: 1 x 1c 17 x 2c 9 x 5c 1 x 20c 
Combination 4259: 1 x 1c 17 x 2c 7 x 5c 3 x 10c 
Combination 4260: 1 x 1c 17 x 2c 7 x 5c 1 x 10c 1 x 20c 
Combination 4261: 1 x 1c 17 x 2c 5 x 5c 4 x 10c 
Combination 4262: 1 x 1c 17 x 2c 5 x 5c 2 x 10c 1 x 20c 
Combination 4263: 1 x 1c 17 x 2c 5 x 5c 2 x 20c 
Combination 4264: 1 x 1c 17 x 2c 3 x 5c 5 x 10c 
Combination 4265: 1 x 1c 17 x 2c 3 x 5c 3 x 10c 1 x 20c 
Combination 4266: 1 x 1c 17 x 2c 3 x 5c 1 x 10c 2 x 20c 
Combination 4267: 1 x 1c 17 x 2c 3 x 5c 1 x 50c 
Combination 4268: 1 x 1c 17 x 2c 1 x 5c 6 x 10c 
Combination 4269: 1 x 1c 17 x 2c 1 x 5c 4 x 10c 1 x 20c 
Combination 4270: 1 x 1c 17 x 2c 1 x 5c 2 x 10c 2 x 20c 
Combination 4271: 1 x 1c 17 x 2c 1 x 5c 1 x 10c 1 x 50c 
Combination 4272: 1 x 1c 17 x 2c 1 x 5c 3 x 20c 
Combination 4273: 1 x 1c 12 x 2c 15 x 5c 
Combination 4274: 1 x 1c 12 x 2c 13 x 5c 1 x 10c 
Combination 4275: 1 x 1c 12 x 2c 11 x 5c 2 x 10c 
Combination 4276: 1 x 1c 12 x 2c 11 x 5c 1 x 20c 
Combination 4277: 1 x 1c 12 x 2c 9 x 5c 3 x 10c 
Combination 4278: 1 x 1c 12 x 2c 9 x 5c 1 x 10c 1 x 20c 
Combination 4279: 1 x 1c 12 x 2c 7 x 5c 4 x 10c 
Combination 4280: 1 x 1c 12 x 2c 7 x 5c 2 x 10c 1 x 20c 
Combination 4281: 1 x 1c 12 x 2c 7 x 5c 2 x 20c 
Combination 4282: 1 x 1c 12 x 2c 5 x 5c 5 x 10c 
Combination 4283: 1 x 1c 12 x 2c 5 x 5c 3 x 10c 1 x 20c 
Combination 4284: 1 x 1c 12 x 2c 5 x 5c 1 x 10c 2 x 20c 
Combination 4285: 1 x 1c 12 x 2c 5 x 5c 1 x 50c 
Combination 4286: 1 x 1c 12 x 2c 3 x 5c 6 x 10c 
Combination 4287: 1 x 1c 12 x 2c 3 x 5c 4 x 10c 1 x 20c 
Combination 4288: 1 x 1c 12 x 2c 3 x 5c 2 x 10c 2 x 20c 
Combination 4289: 1 x 1c 12 x 2c 3 x 5c 1 x 10c 1 x 50c 
Combination 4290: 1 x 1c 12 x 2c 3 x 5c 3 x 20c 
Combination 4291: 1 x 1c 12 x 2c 1 x 5c 7 x 10c 
Combination 4292: 1 x 1c 12 x 2c 1 x 5c 5 x 10c 1 x 20c 
Combination 4293: 1 x 1c 12 x 2c 1 x 5c 3 x 10c 2 x 20c 
Combination 4294: 1 x 1c 12 x 2c 1 x 5c 2 x 10c 1 x 50c 
Combination 4295: 1 x 1c 12 x 2c 1 x 5c 1 x 10c 3 x 20c 
Combination 4296: 1 x 1c 12 x 2c 1 x 5c 1 x 20c 1 x 50c 
Combination 4297: 1 x 1c 7 x 2c 17 x 5c 
Combination 4298: 1 x 1c 7 x 2c 15 x 5c 1 x 10c 
Combination 4299: 1 x 1c 7 x 2c 13 x 5c 2 x 10c 
Combination 4300: 1 x 1c 7 x 2c 13 x 5c 1 x 20c 
Combination 4301: 1 x 1c 7 x 2c 11 x 5c 3 x 10c 
Combination 4302: 1 x 1c 7 x 2c 11 x 5c 1 x 10c 1 x 20c 
Combination 4303: 1 x 1c 7 x 2c 9 x 5c 4 x 10c 
Combination 4304: 1 x 1c 7 x 2c 9 x 5c 2 x 10c 1 x 20c 
Combination 4305: 1 x 1c 7 x 2c 9 x 5c 2 x 20c 
Combination 4306: 1 x 1c 7 x 2c 7 x 5c 5 x 10c 
Combination 4307: 1 x 1c 7 x 2c 7 x 5c 3 x 10c 1 x 20c 
Combination 4308: 1 x 1c 7 x 2c 7 x 5c 1 x 10c 2 x 20c 
Combination 4309: 1 x 1c 7 x 2c 7 x 5c 1 x 50c 
Combination 4310: 1 x 1c 7 x 2c 5 x 5c 6 x 10c 
Combination 4311: 1 x 1c 7 x 2c 5 x 5c 4 x 10c 1 x 20c 
Combination 4312: 1 x 1c 7 x 2c 5 x 5c 2 x 10c 2 x 20c 
Combination 4313: 1 x 1c 7 x 2c 5 x 5c 1 x 10c 1 x 50c 
Combination 4314: 1 x 1c 7 x 2c 5 x 5c 3 x 20c 
Combination 4315: 1 x 1c 7 x 2c 3 x 5c 7 x 10c 
Combination 4316: 1 x 1c 7 x 2c 3 x 5c 5 x 10c 1 x 20c 
Combination 4317: 1 x 1c 7 x 2c 3 x 5c 3 x 10c 2 x 20c 
Combination 4318: 1 x 1c 7 x 2c 3 x 5c 2 x 10c 1 x 50c 
Combination 4319: 1 x 1c 7 x 2c 3 x 5c 1 x 10c 3 x 20c 
Combination 4320: 1 x 1c 7 x 2c 3 x 5c 1 x 20c 1 x 50c 
Combination 4321: 1 x 1c 7 x 2c 1 x 5c 8 x 10c 
Combination 4322: 1 x 1c 7 x 2c 1 x 5c 6 x 10c 1 x 20c 
Combination 4323: 1 x 1c 7 x 2c 1 x 5c 4 x 10c 2 x 20c 
Combination 4324: 1 x 1c 7 x 2c 1 x 5c 3 x 10c 1 x 50c 
Combination 4325: 1 x 1c 7 x 2c 1 x 5c 2 x 10c 3 x 20c 
Combination 4326: 1 x 1c 7 x 2c 1 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4327: 1 x 1c 7 x 2c 1 x 5c 4 x 20c 
Combination 4328: 1 x 1c 2 x 2c 19 x 5c 
Combination 4329: 1 x 1c 2 x 2c 17 x 5c 1 x 10c 
Combination 4330: 1 x 1c 2 x 2c 15 x 5c 2 x 10c 
Combination 4331: 1 x 1c 2 x 2c 15 x 5c 1 x 20c 
Combination 4332: 1 x 1c 2 x 2c 13 x 5c 3 x 10c 
Combination 4333: 1 x 1c 2 x 2c 13 x 5c 1 x 10c 1 x 20c 
Combination 4334: 1 x 1c 2 x 2c 11 x 5c 4 x 10c 
Combination 4335: 1 x 1c 2 x 2c 11 x 5c 2 x 10c 1 x 20c 
Combination 4336: 1 x 1c 2 x 2c 11 x 5c 2 x 20c 
Combination 4337: 1 x 1c 2 x 2c 9 x 5c 5 x 10c 
Combination 4338: 1 x 1c 2 x 2c 9 x 5c 3 x 10c 1 x 20c 
Combination 4339: 1 x 1c 2 x 2c 9 x 5c 1 x 10c 2 x 20c 
Combination 4340: 1 x 1c 2 x 2c 9 x 5c 1 x 50c 
Combination 4341: 1 x 1c 2 x 2c 7 x 5c 6 x 10c 
Combination 4342: 1 x 1c 2 x 2c 7 x 5c 4 x 10c 1 x 20c 
Combination 4343: 1 x 1c 2 x 2c 7 x 5c 2 x 10c 2 x 20c 
Combination 4344: 1 x 1c 2 x 2c 7 x 5c 1 x 10c 1 x 50c 
Combination 4345: 1 x 1c 2 x 2c 7 x 5c 3 x 20c 
Combination 4346: 1 x 1c 2 x 2c 5 x 5c 7 x 10c 
Combination 4347: 1 x 1c 2 x 2c 5 x 5c 5 x 10c 1 x 20c 
Combination 4348: 1 x 1c 2 x 2c 5 x 5c 3 x 10c 2 x 20c 
Combination 4349: 1 x 1c 2 x 2c 5 x 5c 2 x 10c 1 x 50c 
Combination 4350: 1 x 1c 2 x 2c 5 x 5c 1 x 10c 3 x 20c 
Combination 4351: 1 x 1c 2 x 2c 5 x 5c 1 x 20c 1 x 50c 
Combination 4352: 1 x 1c 2 x 2c 3 x 5c 8 x 10c 
Combination 4353: 1 x 1c 2 x 2c 3 x 5c 6 x 10c 1 x 20c 
Combination 4354: 1 x 1c 2 x 2c 3 x 5c 4 x 10c 2 x 20c 
Combination 4355: 1 x 1c 2 x 2c 3 x 5c 3 x 10c 1 x 50c 
Combination 4356: 1 x 1c 2 x 2c 3 x 5c 2 x 10c 3 x 20c 
Combination 4357: 1 x 1c 2 x 2c 3 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4358: 1 x 1c 2 x 2c 3 x 5c 4 x 20c 
Combination 4359: 1 x 1c 2 x 2c 1 x 5c 9 x 10c 
Combination 4360: 1 x 1c 2 x 2c 1 x 5c 7 x 10c 1 x 20c 
Combination 4361: 1 x 1c 2 x 2c 1 x 5c 5 x 10c 2 x 20c 
Combination 4362: 1 x 1c 2 x 2c 1 x 5c 4 x 10c 1 x 50c 
Combination 4363: 1 x 1c 2 x 2c 1 x 5c 3 x 10c 3 x 20c 
Combination 4364: 1 x 1c 2 x 2c 1 x 5c 2 x 10c 1 x 20c 1 x 50c 
Combination 4365: 1 x 1c 2 x 2c 1 x 5c 1 x 10c 4 x 20c 
Combination 4366: 1 x 1c 2 x 2c 1 x 5c 2 x 20c 1 x 50c 
Combination 4367: 50 x 2c 
Combination 4368: 45 x 2c 2 x 5c 
Combination 4369: 45 x 2c 1 x 10c 
Combination 4370: 40 x 2c 4 x 5c 
Combination 4371: 40 x 2c 2 x 5c 1 x 10c 
Combination 4372: 40 x 2c 2 x 10c 
Combination 4373: 40 x 2c 1 x 20c 
Combination 4374: 35 x 2c 6 x 5c 
Combination 4375: 35 x 2c 4 x 5c 1 x 10c 
Combination 4376: 35 x 2c 2 x 5c 2 x 10c 
Combination 4377: 35 x 2c 2 x 5c 1 x 20c 
Combination 4378: 35 x 2c 3 x 10c 
Combination 4379: 35 x 2c 1 x 10c 1 x 20c 
Combination 4380: 30 x 2c 8 x 5c 
Combination 4381: 30 x 2c 6 x 5c 1 x 10c 
Combination 4382: 30 x 2c 4 x 5c 2 x 10c 
Combination 4383: 30 x 2c 4 x 5c 1 x 20c 
Combination 4384: 30 x 2c 2 x 5c 3 x 10c 
Combination 4385: 30 x 2c 2 x 5c 1 x 10c 1 x 20c 
Combination 4386: 30 x 2c 4 x 10c 
Combination 4387: 30 x 2c 2 x 10c 1 x 20c 
Combination 4388: 30 x 2c 2 x 20c 
Combination 4389: 25 x 2c 10 x 5c 
Combination 4390: 25 x 2c 8 x 5c 1 x 10c 
Combination 4391: 25 x 2c 6 x 5c 2 x 10c 
Combination 4392: 25 x 2c 6 x 5c 1 x 20c 
Combination 4393: 25 x 2c 4 x 5c 3 x 10c 
Combination 4394: 25 x 2c 4 x 5c 1 x 10c 1 x 20c 
Combination 4395: 25 x 2c 2 x 5c 4 x 10c 
Combination 4396: 25 x 2c 2 x 5c 2 x 10c 1 x 20c 
Combination 4397: 25 x 2c 2 x 5c 2 x 20c 
Combination 4398: 25 x 2c 5 x 10c 
Combination 4399: 25 x 2c 3 x 10c 1 x 20c 
Combination 4400: 25 x 2c 1 x 10c 2 x 20c 
Combination 4401: 25 x 2c 1 x 50c 
Combination 4402: 20 x 2c 12 x 5c 
Combination 4403: 20 x 2c 10 x 5c 1 x 10c 
Combination 4404: 20 x 2c 8 x 5c 2 x 10c 
Combination 4405: 20 x 2c 8 x 5c 1 x 20c 
Combination 4406: 20 x 2c 6 x 5c 3 x 10c 
Combination 4407: 20 x 2c 6 x 5c 1 x 10c 1 x 20c 
Combination 4408: 20 x 2c 4 x 5c 4 x 10c 
Combination 4409: 20 x 2c 4 x 5c 2 x 10c 1 x 20c 
Combination 4410: 20 x 2c 4 x 5c 2 x 20c 
Combination 4411: 20 x 2c 2 x 5c 5 x 10c 
Combination 4412: 20 x 2c 2 x 5c 3 x 10c 1 x 20c 
Combination 4413: 20 x 2c 2 x 5c 1 x 10c 2 x 20c 
Combination 4414: 20 x 2c 2 x 5c 1 x 50c 
Combination 4415: 20 x 2c 6 x 10c 
Combination 4416: 20 x 2c 4 x 10c 1 x 20c 
Combination 4417: 20 x 2c 2 x 10c 2 x 20c 
Combination 4418: 20 x 2c 1 x 10c 1 x 50c 
Combination 4419: 20 x 2c 3 x 20c 
Combination 4420: 15 x 2c 14 x 5c 
Combination 4421: 15 x 2c 12 x 5c 1 x 10c 
Combination 4422: 15 x 2c 10 x 5c 2 x 10c 
Combination 4423: 15 x 2c 10 x 5c 1 x 20c 
Combination 4424: 15 x 2c 8 x 5c 3 x 10c 
Combination 4425: 15 x 2c 8 x 5c 1 x 10c 1 x 20c 
Combination 4426: 15 x 2c 6 x 5c 4 x 10c 
Combination 4427: 15 x 2c 6 x 5c 2 x 10c 1 x 20c 
Combination 4428: 15 x 2c 6 x 5c 2 x 20c 
Combination 4429: 15 x 2c 4 x 5c 5 x 10c 
Combination 4430: 15 x 2c 4 x 5c 3 x 10c 1 x 20c 
Combination 4431: 15 x 2c 4 x 5c 1 x 10c 2 x 20c 
Combination 4432: 15 x 2c 4 x 5c 1 x 50c 
Combination 4433: 15 x 2c 2 x 5c 6 x 10c 
Combination 4434: 15 x 2c 2 x 5c 4 x 10c 1 x 20c 
Combination 4435: 15 x 2c 2 x 5c 2 x 10c 2 x 20c 
Combination 4436: 15 x 2c 2 x 5c 1 x 10c 1 x 50c 
Combination 4437: 15 x 2c 2 x 5c 3 x 20c 
Combination 4438: 15 x 2c 7 x 10c 
Combination 4439: 15 x 2c 5 x 10c 1 x 20c 
Combination 4440: 15 x 2c 3 x 10c 2 x 20c 
Combination 4441: 15 x 2c 2 x 10c 1 x 50c 
Combination 4442: 15 x 2c 1 x 10c 3 x 20c 
Combination 4443: 15 x 2c 1 x 20c 1 x 50c 
Combination 4444: 10 x 2c 16 x 5c 
Combination 4445: 10 x 2c 14 x 5c 1 x 10c 
Combination 4446: 10 x 2c 12 x 5c 2 x 10c 
Combination 4447: 10 x 2c 12 x 5c 1 x 20c 
Combination 4448: 10 x 2c 10 x 5c 3 x 10c 
Combination 4449: 10 x 2c 10 x 5c 1 x 10c 1 x 20c 
Combination 4450: 10 x 2c 8 x 5c 4 x 10c 
Combination 4451: 10 x 2c 8 x 5c 2 x 10c 1 x 20c 
Combination 4452: 10 x 2c 8 x 5c 2 x 20c 
Combination 4453: 10 x 2c 6 x 5c 5 x 10c 
Combination 4454: 10 x 2c 6 x 5c 3 x 10c 1 x 20c 
Combination 4455: 10 x 2c 6 x 5c 1 x 10c 2 x 20c 
Combination 4456: 10 x 2c 6 x 5c 1 x 50c 
Combination 4457: 10 x 2c 4 x 5c 6 x 10c 
Combination 4458: 10 x 2c 4 x 5c 4 x 10c 1 x 20c 
Combination 4459: 10 x 2c 4 x 5c 2 x 10c 2 x 20c 
Combination 4460: 10 x 2c 4 x 5c 1 x 10c 1 x 50c 
Combination 4461: 10 x 2c 4 x 5c 3 x 20c 
Combination 4462: 10 x 2c 2 x 5c 7 x 10c 
Combination 4463: 10 x 2c 2 x 5c 5 x 10c 1 x 20c 
Combination 4464: 10 x 2c 2 x 5c 3 x 10c 2 x 20c 
Combination 4465: 10 x 2c 2 x 5c 2 x 10c 1 x 50c 
Combination 4466: 10 x 2c 2 x 5c 1 x 10c 3 x 20c 
Combination 4467: 10 x 2c 2 x 5c 1 x 20c 1 x 50c 
Combination 4468: 10 x 2c 8 x 10c 
Combination 4469: 10 x 2c 6 x 10c 1 x 20c 
Combination 4470: 10 x 2c 4 x 10c 2 x 20c 
Combination 4471: 10 x 2c 3 x 10c 1 x 50c 
Combination 4472: 10 x 2c 2 x 10c 3 x 20c 
Combination 4473: 10 x 2c 1 x 10c 1 x 20c 1 x 50c 
Combination 4474: 10 x 2c 4 x 20c 
Combination 4475: 5 x 2c 18 x 5c 
Combination 4476: 5 x 2c 16 x 5c 1 x 10c 
Combination 4477: 5 x 2c 14 x 5c 2 x 10c 
Combination 4478: 5 x 2c 14 x 5c 1 x 20c 
Combination 4479: 5 x 2c 12 x 5c 3 x 10c 
Combination 4480: 5 x 2c 12 x 5c 1 x 10c 1 x 20c 
Combination 4481: 5 x 2c 10 x 5c 4 x 10c 
Combination 4482: 5 x 2c 10 x 5c 2 x 10c 1 x 20c 
Combination 4483: 5 x 2c 10 x 5c 2 x 20c 
Combination 4484: 5 x 2c 8 x 5c 5 x 10c 
Combination 4485: 5 x 2c 8 x 5c 3 x 10c 1 x 20c 
Combination 4486: 5 x 2c 8 x 5c 1 x 10c 2 x 20c 
Combination 4487: 5 x 2c 8 x 5c 1 x 50c 
Combination 4488: 5 x 2c 6 x 5c 6 x 10c 
Combination 4489: 5 x 2c 6 x 5c 4 x 10c 1 x 20c 
Combination 4490: 5 x 2c 6 x 5c 2 x 10c 2 x 20c 
Combination 4491: 5 x 2c 6 x 5c 1 x 10c 1 x 50c 
Combination 4492: 5 x 2c 6 x 5c 3 x 20c 
Combination 4493: 5 x 2c 4 x 5c 7 x 10c 
Combination 4494: 5 x 2c 4 x 5c 5 x 10c 1 x 20c 
Combination 4495: 5 x 2c 4 x 5c 3 x 10c 2 x 20c 
Combination 4496: 5 x 2c 4 x 5c 2 x 10c 1 x 50c 
Combination 4497: 5 x 2c 4 x 5c 1 x 10c 3 x 20c 
Combination 4498: 5 x 2c 4 x 5c 1 x 20c 1 x 50c 
Combination 4499: 5 x 2c 2 x 5c 8 x 10c 
Combination 4500: 5 x 2c 2 x 5c 6 x 10c 1 x 20c 
Combination 4501: 5 x 2c 2 x 5c 4 x 10c 2 x 20c 
Combination 4502: 5 x 2c 2 x 5c 3 x 10c 1 x 50c 
Combination 4503: 5 x 2c 2 x 5c 2 x 10c 3 x 20c 
Combination 4504: 5 x 2c 2 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4505: 5 x 2c 2 x 5c 4 x 20c 
Combination 4506: 5 x 2c 9 x 10c 
Combination 4507: 5 x 2c 7 x 10c 1 x 20c 
Combination 4508: 5 x 2c 5 x 10c 2 x 20c 
Combination 4509: 5 x 2c 4 x 10c 1 x 50c 
Combination 4510: 5 x 2c 3 x 10c 3 x 20c 
Combination 4511: 5 x 2c 2 x 10c 1 x 20c 1 x 50c 
Combination 4512: 5 x 2c 1 x 10c 4 x 20c 
Combination 4513: 5 x 2c 2 x 20c 1 x 50c 
Combination 4514: 20 x 5c 
Combination 4515: 18 x 5c 1 x 10c 
Combination 4516: 16 x 5c 2 x 10c 
Combination 4517: 16 x 5c 1 x 20c 
Combination 4518: 14 x 5c 3 x 10c 
Combination 4519: 14 x 5c 1 x 10c 1 x 20c 
Combination 4520: 12 x 5c 4 x 10c 
Combination 4521: 12 x 5c 2 x 10c 1 x 20c 
Combination 4522: 12 x 5c 2 x 20c 
Combination 4523: 10 x 5c 5 x 10c 
Combination 4524: 10 x 5c 3 x 10c 1 x 20c 
Combination 4525: 10 x 5c 1 x 10c 2 x 20c 
Combination 4526: 10 x 5c 1 x 50c 
Combination 4527: 8 x 5c 6 x 10c 
Combination 4528: 8 x 5c 4 x 10c 1 x 20c 
Combination 4529: 8 x 5c 2 x 10c 2 x 20c 
Combination 4530: 8 x 5c 1 x 10c 1 x 50c 
Combination 4531: 8 x 5c 3 x 20c 
Combination 4532: 6 x 5c 7 x 10c 
Combination 4533: 6 x 5c 5 x 10c 1 x 20c 
Combination 4534: 6 x 5c 3 x 10c 2 x 20c 
Combination 4535: 6 x 5c 2 x 10c 1 x 50c 
Combination 4536: 6 x 5c 1 x 10c 3 x 20c 
Combination 4537: 6 x 5c 1 x 20c 1 x 50c 
Combination 4538: 4 x 5c 8 x 10c 
Combination 4539: 4 x 5c 6 x 10c 1 x 20c 
Combination 4540: 4 x 5c 4 x 10c 2 x 20c 
Combination 4541: 4 x 5c 3 x 10c 1 x 50c 
Combination 4542: 4 x 5c 2 x 10c 3 x 20c 
Combination 4543: 4 x 5c 1 x 10c 1 x 20c 1 x 50c 
Combination 4544: 4 x 5c 4 x 20c 
Combination 4545: 2 x 5c 9 x 10c 
Combination 4546: 2 x 5c 7 x 10c 1 x 20c 
Combination 4547: 2 x 5c 5 x 10c 2 x 20c 
Combination 4548: 2 x 5c 4 x 10c 1 x 50c 
Combination 4549: 2 x 5c 3 x 10c 3 x 20c 
Combination 4550: 2 x 5c 2 x 10c 1 x 20c 1 x 50c 
Combination 4551: 2 x 5c 1 x 10c 4 x 20c 
Combination 4552: 2 x 5c 2 x 20c 1 x 50c 
Combination 4553: 10 x 10c 
Combination 4554: 8 x 10c 1 x 20c 
Combination 4555: 6 x 10c 2 x 20c 
Combination 4556: 5 x 10c 1 x 50c 
Combination 4557: 4 x 10c 3 x 20c 
Combination 4558: 3 x 10c 1 x 20c 1 x 50c 
Combination 4559: 2 x 10c 4 x 20c 
Combination 4560: 1 x 10c 2 x 20c 1 x 50c 
Combination 4561: 5 x 20c 
Combination 4562: 2 x 50c 
Combination 4563: 1 x 100c 
Total elapsed time: 52 ms
*/