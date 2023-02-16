package yasu.lavaplayer;

import yasu.lavaplayer.ExecuteArgs;
public interface ICommand {
        void execute(ExecuteArgs var1);

        String getName();

        String helpMessage();

        boolean needOwner();
    }


