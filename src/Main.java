//dot -Tpng graph.dot -o gr.png

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static ArrayList<double[]> arr = new ArrayList<>();
    private static Map<Double, Double[]> param = new HashMap<>();
    private static ArrayList<ArrayList<Double>> paths = new ArrayList<>();

    //i, j, tau, resPoln, resSvob
    private static ArrayList<double[]> reserve = new ArrayList<>();

    private static double first = -1, last = 999;

    public static void main(String[] args)
    {
        arr.add(new double[]{15, 8, 2});
        arr.add(new double[]{14, 6, 1});
        arr.add(new double[]{10, 2, 2});
        arr.add(new double[]{17, 8, 2});
        arr.add(new double[]{3, 14, 3});
        arr.add(new double[]{2, 15, 3});
        arr.add(new double[]{10, 14, 5});
        arr.add(new double[]{2, 6, 4});
        arr.add(new double[]{10, 3, 4});
        arr.add(new double[]{6, 8, 4});
        arr.add(new double[]{3, 6, 2});
        arr.add(new double[]{6, 17, 1});
        arr.add(new double[]{3, 17, 6});
        arr.add(new double[]{17, 3, 0});

//        arr.add(new double[]{1, 2, 9});
//        arr.add(new double[]{2, 3, 9});
//        arr.add(new double[]{3, 4, 99});
//        arr.add(new double[]{4, 2, 9});
//        arr.add(new double[]{4, 5, 99});
//        arr.add(new double[]{1, 5, 99});

        boolean whil = true;

        while (whil) {
            countEventsParam();

            if (param.get(first)[1] == 0) {
                System.out.println("Complete");
                countReserve();
                System.out.println("Tkr = " + param.get(last)[1]);
                makeGraph();
                return;
            } else {
                System.out.println("Error");
                findCycles(first, new ArrayList<>());
                whil = dialog();
            }
        }
    }

    private static void countEventsParam() {
        findFirstAndLast();

        for (int i = 0; i < arr.size(); i++) {
            param.put(arr.get(i)[0], new Double[]{-1.0, -1.0});
            param.put(arr.get(i)[1], new Double[]{-1.0, -1.0});
        }

        //прямой ход
        param.get(first)[0] = 0.0;
        ArrayList<Double> out = outcome(first);
        for (Double d : out)
            forward(d);

        //обратный ход
        param.get(last)[1] = param.get(last)[0];
        ArrayList<Double> in = income(last);
        for(Double d : in)
            back(d);
    }

    private static void displayCycles(){
        for (ArrayList<Double> mas: paths)
        {
            System.out.print("\nCycle: ");

            for (Double var : mas)
                System.out.print(var + " ");
        }
    }

    private static boolean contain(ArrayList<Double> arrayList, Double value) {
        for(Double d : arrayList)
            if (d.doubleValue() == value.doubleValue())
                return true;
        return false;
    }

    private static void findFirstAndLast() {
        //find first
        ArrayList<Double> firsts = new ArrayList<>();
        boolean isFirst = true;
        for (int i = 0; i < arr.size(); i++)
        {
            isFirst = true;
            for (int j = 0; j < arr.size(); j++)
                if (arr.get(i)[0] == arr.get(j)[1])
                    isFirst = false;

            if (isFirst && !contain(firsts, arr.get(i)[0])) {
                firsts.add(arr.get(i)[0]);
            }
        }

        if (firsts.size() > 1)
            for (int i = 0; i < firsts.size(); i++)
                arr.add(new double[]{first, firsts.get(i), 0});
        else first = firsts.get(0);

        //find last
        ArrayList<Double> lasts = new ArrayList<>();
        boolean isLast = true;
        for (int i = 0; i < arr.size(); i++)
        {
            isLast = true;
            for (int j = 0; j < arr.size(); j++)
                if (arr.get(i)[1] == arr.get(j)[0])
                    isLast = false;

            if (isLast && !contain(lasts, arr.get(i)[1]))
                lasts.add(arr.get(i)[1]);
        }

        if (lasts.size() > 1)
            for (int i = 0; i < lasts.size(); i++)
                arr.add(new double[]{lasts.get(i), last, 0});
        else last = lasts.get(0);
    }

    private static void forward(Double node) {
        if (param.get(node)[0] != -1)
            return;
        ArrayList<Double> in = income(node);
        double max = 0;
        for (int i = 0; i < in.size(); i++)
        {
            if (param.get(in.get(i))[0] != -1) {
                if (param.get(in.get(i))[0] + tau(in.get(i), node) > max)
                    max = param.get(in.get(i))[0] + tau(in.get(i), node);
            }
            else return;
        }
        param.get(node)[0] = max;

        ArrayList<Double> out = outcome(node);
        for (Double d : out)
            forward(d);
    }

    private static ArrayList<Double> income(double node) {
        ArrayList<Double> in = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++)
            if (arr.get(i)[1] == node)
                in.add(arr.get(i)[0]);
        return in;
    }

    private static ArrayList<Double> outcome(double node) {
        ArrayList<Double> out = new ArrayList<>();
        for(int i = 0; i < arr.size(); i++)
            if (arr.get(i)[0] == node)
                out.add(arr.get(i)[1]);
        return out;
    }

    private static double tau(double i, double j){
        for (int k = 0; k < arr.size(); k++)
            if (arr.get(k)[0] == i && arr.get(k)[1] == j)
                return arr.get(k)[2];
        return 0;
    }

    private static void back(Double node){
        if(param.get(node)[1] != -1)
            return;

        ArrayList<Double> out = outcome(node);
        double min = param.get(last)[1];
        for (int i = 0; i < out.size(); i++)
        {
            if (param.get(out.get(i))[1] != -1) {
                if (param.get(out.get(i))[1] - tau(node, out.get(i)) < min)
                    min = param.get(out.get(i))[1] - tau(node, out.get(i));
            }
            else return;
        }
        param.get(node)[1] = min;

        ArrayList<Double> in = income(node);
        for (Double d : in)
            back(d);
    }

    private static void makeGraph() {
        try{
            FileWriter writer = new FileWriter("F:\\Projects\\java\\networkgraph\\graph\\graph.dot", false);
            writer.write("digraph{\n");
            //------------------------

            for (Double d : param.keySet())
                writer.write( d.intValue() + " [label=\"index=" + d.intValue() +
                        ", tr=" + param.get(d)[0] + ", tp=" + param.get(d)[1] + ", R=" + (param.get(d)[1] - param.get(d)[0]) + "\"];\n");

            for (int i = 0; i < arr.size(); i++) {

                String color = reserve.get(i)[3] == 0 ? "red" : "black";

                writer.write((int) arr.get(i)[0] + "->" + (int) arr.get(i)[1] +
                        " [label=\"tau=" + (int) arr.get(i)[2] + ", Rp=" + reserve.get(i)[3] + ", Rs=" + reserve.get(i)[4] + "\" color= " + color + "];\n");
            }

            //------------------------
            writer.write("\n}");

            writer.flush();
        }
        catch (IOException e) {
            System.out.println("File not found");
        }
    }

    private static void findCycles(double node, ArrayList<Double> path) {

        if (contain(path, node))
        {
            //нашли цикл
            checkPath(path, node);

            return;
        }
        else path.add(node);

        ArrayList<Double> out = outcome(node);

        for (int i = 0; i < out.size(); i++)
        {
            //если петля
            if (out.get(i) == node) {}

            findCycles(out.get(i), path);
        }

        path.remove(path.size()-1);
    }

    private static void checkPath(ArrayList<Double> path, double node) {
        ArrayList<Double> temp = (ArrayList<Double>) path.clone();

        //выделение непосредственно цикла
        for (int i = 0 ; i < temp.size(); i++)
        {
            if (temp.get(i) != node)
            {
                temp.remove(i);
                i--;
            }
            else
            {
                //temp.add(node);
                break;
            }
        }

        //если список путей пуст
        if (paths.size() == 0) {
            temp.add(node);
            paths.add(temp);
            return;
        }

        // проверка, найден ли уже этот путь
        boolean contain = false;
        for (ArrayList<Double> p : paths)
        {
            ArrayList<Double> temp2 = (ArrayList<Double>) temp.clone();

            if (temp2.size() == p.size()-1)
                for (int i = 0 ; i < p.size()-1; i++)
                    temp2.remove(p.get(i));
            else
                continue;

            if (temp2.size() == 0)
                contain = true;
        }

        if (!contain) {
            temp.add(node);
            paths.add(temp);
        }
    }

    private static void removeEdge(double start, double finish){
        for (double[] par: arr)
            if (par[0] == start && par[1] == finish)
            {

                arr.remove(par);
                return;
            }
    }

    private static boolean dialog() {
        displayCycles();

        System.out.println("\nDo you want to delete any rib? (y/n)");
        Scanner scanner = new Scanner(System.in);

        String answer = scanner.next();
        int answerNum;
        if (answer.equals("y")) {
            do {
                System.out.print("Choose cycle: ");
                answerNum = scanner.nextInt();
            }while( answerNum < 1 || answerNum > paths.size());
            answerNum--;

            ArrayList<Double> cycle = paths.get(answerNum);

            for (int i = 0; i < cycle.size() - 1; i++)
                System.out.println((i + 1) + ": " + cycle.get(i) + " --- " + cycle.get(i+1));

            System.out.println("Choose rib:");
            answerNum = scanner.nextInt();

            removeEdge(cycle.get(answerNum - 1), cycle.get( answerNum));
            return true;
        }
        return false;
    }

    private static void countReserve() {

        if (reserve.size() != arr.size()) {
            reserve.clear();
            for (int i = 0; i < arr.size(); i++)
                reserve.add(new double[]{arr.get(i)[0], arr.get(i)[1], arr.get(i)[2], 0, 0});
        }

        for (int i = 0; i < reserve.size(); i++)
        {
            double resPoln = param.get(reserve.get(i)[1])[1] - param.get(reserve.get(i)[0])[0] - reserve.get(i)[2];
            double resSvob = param.get(reserve.get(i)[1])[0] - param.get(reserve.get(i)[0])[1] - reserve.get(i)[2];

            reserve.get(i)[3] = resPoln;
            reserve.get(i)[4] = resSvob;
        }
    }

}
