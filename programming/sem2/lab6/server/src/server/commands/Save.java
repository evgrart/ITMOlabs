package server.commands;

import server.utility.CollectionManager;
import server.utility.HistoryParser;
import server.utility.Parser;

public class Save extends Command {
    public static void execute() {
        CollectionManager.commandsList.add("save");
        HistoryParser.parseToFile();
        Parser.toJson();
    }
}
