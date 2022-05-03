package com.company;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


class ExpertMethods {
    private void printWithChar(double[][] matrix, int experts, int factors) {
        for (int i = 0; i < experts; i++) System.out.printf("%5.1s ", (char) (i + 65));
        System.out.println();
        for (int i = 0; i < factors; i++) {
            for (int j = 0; j < experts; j++) {
                if (j == 0) System.out.print((char) (i + 65));
                System.out.printf("%5.1f ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public void printWithInt(double[][] matrix, int experts, int factors) {
        for (int i = 0; i < experts; i++) System.out.printf("%5.1s ", (char) (i + 65));
        System.out.println();
        for (int i = 0; i < factors; i++) {
            for (int j = 0; j < experts; j++) {
                if (j == 0) System.out.print(i + 1);
                System.out.printf("%5.1f ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    private double[][] countPair(double[][] matrix, int experts, int factors) {
        double counter = 0.0;
        double[][] rangMatrix = new double[experts][experts];
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < experts; j++) {
                for (int k = 0; k < factors; k++) {
                    counter += Math.pow(matrix[k][i] - matrix[k][j], 2);
                }
                counter = 1 - ((6 * counter) / (Math.pow(factors, 3) - factors));
                BigDecimal bd = new BigDecimal(Double.toString(counter));
                bd = bd.setScale(1, RoundingMode.HALF_UP);
                rangMatrix[i][j] = bd.doubleValue();
                counter = 0.0;
            }
        }
        System.out.println("Матрица ранговой корреляции:");

        printWithChar(rangMatrix, experts, experts);
        return rangMatrix;
    }

    public void matchingThresholdMatrix(double[][] matrix, int experts, int factors, double treshHoldValue) {
        double[][] rangMatrix = countPair(matrix, experts, factors);
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < experts; j++) {
                if (rangMatrix[i][j] >= treshHoldValue) {
                    rangMatrix[i][j] = 1;
                } else {
                    rangMatrix[i][j] = 0;
                }
            }
        }
        System.out.println("Способ согласованности. Матрица: ");
        printWithChar(rangMatrix, experts, experts);
    }

    public void printExpertMatrix(int[][] expertMatrix, int factors, int expert) {
        System.out.println("Эксперт " + (char) (expert + 65) + ":");
        for (int i = 0; i < factors; i++) System.out.printf("%5d ", (i + 1));
        System.out.println();
        for (int i = 0; i < factors; i++) {
            for (int j = 0; j < factors; j++) {
                if (j == 0) System.out.print(i + 1);
                System.out.printf("%5d ", expertMatrix[i][j]);
            }
            System.out.println();
        }
    }


    private void unmatchingThresholdMatrix(double[][] matrix, int experts, double threshholdvalue) {
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < experts; j++) {
                if (matrix[i][j] <= threshholdvalue) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
        System.out.println("Совсем итоговая матрица:");
        printWithChar(matrix, experts, experts);
    }


    private void printFinalMatrix(double[][] matrix, int experts, int treshhold) {
        double summ = 0.0;
        double summ1 = 0.0;
        for (int i = 0; i < experts; i++) System.out.printf("%10.2s ", (char) (i + 65));
        System.out.println();
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < experts; j++) {
                if (j == 0) System.out.print((char) (i + 65));
                System.out.printf("%10.2f ", matrix[i][j]);
                summ += matrix[i][j];
            }
            System.out.print(" " + summ);
            summ1 += summ;
            summ = 0.0;
            System.out.println();
        }
        System.out.println("Сумма всех сумм матрицы: " + summ1);
        unmatchingThresholdMatrix(matrix, experts, treshhold);
    }

    private void sumMatrix(int[][][] expertsMatrix, int experts, int factors, int treshhold) {
        double[][] finalMatrix = new double[experts][experts];
        double sum = 0.0;
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < experts; j++) {
                for (int k = 0; k < factors; k++) {
                    for (int p = 0; p < factors; p++) {
                        sum += Math.abs(expertsMatrix[i][k][p] - expertsMatrix[j][k][p]);
                    }
                }
                sum /= 2;
                finalMatrix[i][j] = sum;
                sum = 0.0;
            }
        }
        System.out.println("Сумма:");
        printFinalMatrix(finalMatrix, experts, treshhold);

    }

    public void unmatchingMatrix(double[][] matrix, int experts, int factors, int treshHold) {
        int[][][] expertsMatrix = new int[experts][factors][factors];
        int number = 0;
        for (int i = 0; i < experts; i++) {
            for (int j = 0; j < factors; j++) {
                for (int k = 0; k < factors; k++) {
                    if (matrix[j][i] == matrix[k][i]) {
                        number = 0;
                    } else if (matrix[j][i] > matrix[k][i]) {
                        number = -1;
                    } else if ((matrix[j][i] < matrix[k][i])) {
                        number = 1;
                    }
                    expertsMatrix[i][j][k] = number;
                }
            }
            printExpertMatrix(expertsMatrix[i], factors, i);
        }
        sumMatrix(expertsMatrix, experts, factors, treshHold);

    }

}

class AutomaticClassification {

    private int maxNumber(int[][] matrix, int rows, int column) {
        int maximum = 0;
        for (int i = 0; i < rows; i++) {
            if (maximum < matrix[i][column]) {
                maximum = matrix[i][column];
            }
        }
        return maximum;
    }


    private int[] getRanges(int numberOfClasses, int maxNumber) {
        int[] range = new int[numberOfClasses + 1];
        range[0] = 0;
        for (int i = 1; i <= numberOfClasses; i++) {
            range[i] = range[i - 1] + maxNumber / numberOfClasses;

        }
        return range;
    }

    private void print(int[][] matrix, int numberOfClasses) {
        for (int i = 0; i < numberOfClasses; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private int[][] cutColumn(int[][] matrix, int selectedColumn) {
        int[][] updatedMatrix = new int[matrix.length][];
        int counter = 0;
        for (int i = 0; i < matrix.length; i++) {
            updatedMatrix[i] = new int[matrix[i].length - 1];
            for (int j = 0; j < matrix[i].length; j++) {
                if (j != selectedColumn) {
                    updatedMatrix[i][counter] = matrix[i][j];
                    counter++;
                }
            }
            counter = 0;
        }
        return updatedMatrix;
    }

    private List<List<Integer>> calculatingCenterOfGravity(int[][] matrix, ArrayList<ArrayList<Integer>> classes) {
        double counter = 0.0;
        List<List<Integer>> classesx = new ArrayList<>();
        List<Integer> classx;
        for (ArrayList<Integer> coordinates : classes) {
            classx = new ArrayList<>();
            for (int j = 0; j < matrix[0].length; j++) {
                for (int coordinate : coordinates) {
                    counter += matrix[coordinate][j];
                }
                counter /= coordinates.size();
                classx.add((int) counter);
                counter = 0;
            }
            classesx.add(classx);
        }
        System.out.println(classesx);
        return classesx;
    }


    private void findEquals(ArrayList<Double> counters, double min, int index) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i) == min) {
                System.out.println("M" + " " + (index + 1) + " принадлежит классу " + (char) (i + 65));
            }
        }
    }

    private void min(ArrayList<Double> counters, int index) {
        double min = Double.MAX_VALUE;
        for (double counter : counters) {
            min = Math.min(counter, min);
        }
        findEquals(counters, min, index);
    }


    private void max(ArrayList<Double> counters, int index) {
        double max = Double.MIN_VALUE;
        for (double counter : counters) {
            max = Math.max(max, counter);
        }
        findEquals(counters, max, index);
    }


    // от объектов до центра тяжести классов
    private void Algorigm1(List<List<Integer>> classes, int[][] matrix) {
        double counter = 0.0;
        ArrayList<Double> counters;
        System.out.println("Алгоритм 1:\n");
        for (int i = 0; i < classes.size(); i++) {
            counters = new ArrayList<>();
            for (int j = 0; j < classes.size(); j++) {
                for (int k = 0; k < classes.get(j).size(); k++) {
                    counter += Math.pow(matrix[i][k] - classes.get(j).get(k), 2);
                }
                counters.add(counter);
                System.out.println(" M " + (i + 1) + "," + (char) (j + 65) + " = " + Math.sqrt(counter));
                counter = 0;
            }
            min(counters, i);
        }
    }

    // по расстоянию от объектов до центов тяжести классов с учетом весовых коэфицентов
    private void Algoritm2(List<List<Integer>> classes, int[][] matrix, double[] weight) {
        System.out.println("Алгоритм 2:\n");
        double counter = 0.0;
        ArrayList<Double> counters;
        for (int i = 0; i < classes.size(); i++) {
            counters = new ArrayList<>();
            for (int j = 0; j < classes.size(); j++) {
                for (int k = 0; k < classes.get(j).size(); k++) {
                    counter += weight[k] * Math.pow(matrix[i][k] - classes.get(j).get(k), 2);
                }
                counters.add(counter);
                System.out.println(" M " + (i + 1) + "," + (char) (j + 65) + " = " + Math.sqrt(counter));
                counter = 0;
            }
            min(counters, i);
        }

    }

    // классификация по скалярному произведению
    private double[][] Algoritm3(List<List<Integer>> classes, int[][] matrix) {
        System.out.println("Алгоритм 3:");
        double[][] array = new double[classes.size()][classes.size()];
        double counter = 0.0;
        ArrayList<Double> counters;
        for (int i = 0; i < classes.size(); i++) {
            counters = new ArrayList<>();
            for (int j = 0; j < classes.size(); j++) {
                for (int k = 0; k < classes.get(j).size(); k++) {
                    counter += matrix[i][k] * classes.get(j).get(k);
                }
                System.out.println(" B " + (i + 1) + "," + (char) (j + 65) + " = " + counter);
                counters.add(counter);
                array[i][j] = counter;
                counter = 0;
            }
            max(counters, i);
        }
        return array;
    }

    private double sum(Object[] matrix, int pow) {
        double sum = 0.0;
        for (Object o : matrix) {
            sum += Math.pow(Double.parseDouble(String.valueOf(o)), pow);
        }
        return sum;
    }

    private double sum(int[] matrix, int pow) {
        double sum = 0.0;
        for (int j : matrix) {
            sum += Math.pow(j, pow);
        }
        return sum;
    }

    private void Algoritm4(List<List<Integer>> classes, int[][] matrix, double[][] B) {
        System.out.println("Алгоритм 4:");
        ArrayList<Double> counters;
        double counter;
        for (int i = 0; i < classes.size(); i++) {
            counters = new ArrayList<>();
            for (int j = 0; j < classes.size(); j++) {
                counter = B[i][j] - ((sum(classes.get(j).toArray(), 1) * sum(matrix[i], 1)) / classes.get(i).size());
                System.out.println("R M" + (i + 1) + (char) (j + 65) + " = " + counter);
                counters.add(counter);
            }
            max(counters, i);
        }
    }

    private void Algoritm5(List<List<Integer>> classes, int[][] matrix, double[][] B) {
        System.out.println("Алгоритм 5:");
        double counter;
        ArrayList<Double> counters;
        for (int i = 0; i < classes.size(); i++) {
            counters = new ArrayList<>();
            for (int j = 0; j < classes.size(); j++) {
                counter = B[i][j] / (Math.sqrt(sum(classes.get(j).toArray(), 2)) * Math.sqrt(sum(matrix[i], 2)));
                counters.add(counter);
                System.out.println("COS(Ф) М" + (i + 1) + (char) (j + 65) + " = " + counter);
            }
            max(counters, i);
        }
    }


    public void divisionIntoClasses(int[][] matrix, int selectedColumn, int numberOfClasses, int[][] matrixx, double[] weight) {
        ArrayList<ArrayList<Integer>> classes = new ArrayList<>();
        ArrayList<Integer> subClasses;
        int[] range = getRanges(numberOfClasses, maxNumber(matrix, matrix.length, selectedColumn));
        System.out.println(Arrays.toString(range));
        for (int j = 0; j < numberOfClasses; j++) {
            subClasses = new ArrayList<>();
            for (int i = 0; i < matrix.length; i++) {
                if (!((matrix[i][selectedColumn] < range[j]) || (matrix[i][selectedColumn] > range[j + 1]))) {
                    subClasses.add(i);
                }
            }
            classes.add(subClasses);
        }
        System.out.println(classes);
        print(matrix, matrix.length);
        List<List<Integer>> classesx = calculatingCenterOfGravity(cutColumn(matrix, selectedColumn), classes);
        Algorigm1(classesx, matrixx);
       Algoritm2(classesx, matrixx, weight);
        double[][] B = Algoritm3(classesx, matrixx);
        Algoritm4(classesx, matrixx, B);
        Algoritm5(classesx, matrixx, B);

    }

}

public class Main {


    private static int getNumColumns(Sheet sheet, int rowN) {
        Row row = sheet.getRow(rowN);
        return (int) (row == null ? 0 : row.getLastCellNum());
    }


    private static int[][] readExcelFile(String file) {
        int[][] values = new int[0][];
        try {
            Workbook workbook = WorkbookFactory.create(new File(file));
            Sheet sheet = workbook.getSheetAt(0);
            int numberRows = sheet.getLastRowNum() + 1;
            values = new int[numberRows][];
            for (int i = 0; i < numberRows; i++) {
                Row row = sheet.getRow(i);
                values[i] = new int[getNumColumns(sheet, i)];
                for (int j = 0; j < getNumColumns(sheet, i); j++) {
                    Cell cell = row.getCell(j);
                    values[i][j] = (int) Double.parseDouble(cell.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка парсинга Excel");
            e.printStackTrace();
        }
        return values;
    }


    public static void main(String[] args) {
        double[][] matrix;
        ExpertMethods expertMethods = new ExpertMethods();
        Scanner scan = new Scanner(System.in).useLocale(Locale.US);
        int experts, factors, threshholdvalueunMatching;
        double threshholdvalueMatching;
        String excelFile;
        System.out.println("Введите ссылку на EXCEL файл >> ");
        excelFile = scan.next();
        matrix = readExcelFile(excelFile);
        factors = matrix.length;
        experts = matrix[0].length;
        System.out.println("Исходная матрица:");
        expertMethods.printWithInt(matrix, experts, factors);
        System.out.println("Введите пороговое значение для метода согласования >> ");
        threshholdvalueMatching = scan.nextDouble();
        System.out.println("Метод согласования:");
        expertMethods.matchingThresholdMatrix(matrix, experts, factors, threshholdvalueMatching);
        System.out.println("Введите пороговое значение для метода рассогласования >> ");
        threshholdvalueunMatching = scan.nextInt();
        System.out.println("Метод рассогласования:");
        expertMethods.unmatchingMatrix(matrix, experts, factors, threshholdvalueunMatching);

        // ____________________________________________________________________________________________________ //
        AutomaticClassification classification = new AutomaticClassification();
        int classesLength, selectedColumn;
        System.out.println("Введите количество классов, на которые необходимо разделить >>");
        classesLength = scan.nextInt();
        int[][] classificationMatrix = readExcelFile(excelFile);
        double[] weight = new double[classificationMatrix[0].length - 1];
        int[][] activityMatrix = new int[classesLength][classificationMatrix[0].length - 1];
        System.out.println("Введите весовые коэфиценты >>");
        for (int i = 0; i < weight.length; i++)
            weight[i] = scan.nextDouble();
        System.out.println("Введите матрицу действия >>");
        for (int i = 0; i < activityMatrix.length; i++) {
            for (int j = 0; j < activityMatrix[i].length; j++) {
                activityMatrix[i][j] = scan.nextInt();
            }
        }
        System.out.println("Введите выбранный столбец >> ");
        selectedColumn = scan.nextInt();
        classification.divisionIntoClasses(classificationMatrix, selectedColumn, classesLength, activityMatrix, weight);
    }
}
