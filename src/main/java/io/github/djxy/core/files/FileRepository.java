package io.github.djxy.core.files;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Samuel on 2016-04-02.
 */
public class FileRepository {

    private final ArrayList<FileManager> files;

    public FileRepository(Path folderPath) {
        this.files = new ArrayList<>();
    }

    public void loadFiles(){
        for(FileManager file : files){

        }
    }

    public void saveFiles(){
        for(FileManager file : files){
        }
    }

    private void addFile(FileManager fileContainer){
        files.add(fileContainer);
    }

    private void initFiles(){
    }

}
