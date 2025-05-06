//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.commands;

import server.utility.HistoryParser;

public class Save extends Command {
    public Save() {
    }

    public static void execute() {
        HistoryParser.commandsList.add("save");
        HistoryParser.parseToFile();
    }
}
