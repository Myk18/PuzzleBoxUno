package ua.cn.sandi.puzzleboxuno;

/**
 * Created by mikni on 05.10.2017.
 */

public class Puzzle {

    private static Puzzle instance;

    public static synchronized Puzzle getInstance(){
        if (instance == null) {
            instance = new Puzzle();
        }
        return instance;
    }

    Puzzle(){
    }

}
