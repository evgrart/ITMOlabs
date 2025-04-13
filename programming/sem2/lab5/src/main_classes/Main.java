package main_classes;


import reader_manager.InputManager;
import utility.HistoryParser;
import utility.Parser;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
    public static ZonedDateTime time = ZonedDateTime.now();
    public static ArrayList<String> commandsList = new ArrayList<>();
    public static LinkedList<StudyGroup> groups = new LinkedList<>();
    public static HashMap<Integer, StudyGroup> ids = new HashMap<>();
    public static String FILE_NAME;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Перезапустите программу и укажите путь к файлу!");
            System.exit(0);
        }
        FILE_NAME = args[0];
        Parser.fromJson();
        HistoryParser.parseToList();
        System.out.println("Программа загружена\n");
        InputManager.startInput();
    }
}