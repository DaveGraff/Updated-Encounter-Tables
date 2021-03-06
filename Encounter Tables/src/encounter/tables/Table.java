/*
Dave Graff 2018
 */
package encounter.tables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *8
 * @author David
 */
public class Table implements Serializable{
    private String name = null;
    private Monster[] table;
    private int monsterNum;//number of monsters currently in the table 
    private Monster selectedMonster = null;
    
    public Table(String n){
        name = n;
        table = new Monster[19];
        monsterNum = 0;
    }
    
    public Table(){
        table = new Monster[19];
        monsterNum = 0;
    }
    
    public Table deepCopy(){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream writer = new ObjectOutputStream(baos);
            writer.writeObject(this);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream reader = new ObjectInputStream(bais);
            return (Table) reader.readObject();
        } catch(Exception e){return null;}
    }
    
    public Monster[] getMonsterList(){return table;}
    public void setMonsterList(Monster[] m){table = m;}
    public int getMonsterNum(){return monsterNum;}
    public void setMonsterNum(int i){monsterNum = i;}
    
    //Getters & setters
    public String getName(){
        return name;
    }
    
    public void setName(String n){
        name = n;
    }
    /*
    Returns the user expected index, returns null if
    out of bounds or space is empty
    */
    public Monster getMonster(int index){
        index = index - 2;
        return table[index];
    }
    
    //internal roll method
    private int roll(){
        Random rand = new Random();
        //Operator is exclusive
        return rand.nextInt(8) + rand.nextInt(12) + 2;
    }
    
    /*
    Returns a monster from the table, if there is one
    */
    public Monster executeRoll(){
        if(monsterNum > 0){
            Monster spooky = null;
            while(spooky == null){
                spooky = getMonster(roll());
            }
            return spooky;
        } else return null;
    }
    
    /*
    Adds monster to the specified input, replaces current choice 
    if there is one. Index is assumed in 2-20 format
    */
    public void addMonster(Monster input, int index){
        index = index - 2;
        if(index > -1 && index < 19)
            table[index] = input.deepCopy();
        monsterNum++;
    }
    
    /*
    Removes monster at the specified 2-20 index, will return null
    if out of bounds or unpopulated
    */
    public Monster removeMonster(int index){
        index = index - 2;
        if (index > 1 && index < 21){
            Monster temp = table[index];
            table[index] = null;
            if (temp != null)
                monsterNum--;
            return temp;
        }
        else return null;
    }

    /*
    JavaFX view of the table with options & slider *ooh*
    */
    public ScrollPane tableView(ArrayList<Monster> monsterList, ScrollPane tableView){
        //For each Monster: Index, Add, Remove, Edit, Make Unique
        VBox innerPane = new VBox();
        tableView.setContent(null);
        for(int i = 0; i < 19; i++){
            final int temp = i + 2;
            boolean isNull = false;
            if (table[i] == null)
                isNull = true;
            Label mName;
            if (isNull)
                mName = new Label("EMPTY");
            else
                mName = new Label(table[i].getName());
            mName.setMaxWidth(100);
            mName.setText(mName.getText() + "\t\t\t");//Makes spacing nicer
            Label index = new Label(Integer.toString(temp) + '\t');
            Button add = new Button("Add");
            add.setOnAction(e -> {
                Monster t = monsterSelector(monsterList);
                if(t != null){
                    addMonster(t, temp);
                    tableView(monsterList, tableView);
                }
            });
            HBox row = new HBox(index, mName, add);
            if (!isNull){
                Button remove = new Button("Remove");
                remove.setOnAction(e -> {
                    removeMonster(temp);
                    tableView(monsterList, tableView);
                });
                
                Button edit = new Button("Edit");
                edit.setOnAction(e -> {
                    Monster t = getMonster(temp);
                    String name = t.getName();
                    t.editMonster();
                    if(t.getName() == null)
                        t.setName(name);
                    tableView(monsterList, tableView);
                });
                row.getChildren().addAll(edit, remove);
                row.getChildren().remove(add);
            }
            //Implement make unique later?
            innerPane.getChildren().addAll(row);
        }
        
        tableView.setContent(innerPane);
        tableView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return tableView;
    }
    
    private Monster monsterSelector(ArrayList<Monster> monsterList){
        Stage newStage = new Stage();
        VBox inner = new VBox();
        monsterList.stream().map((monster) -> {
            Button temp = new Button(monster.getName());
            temp.setOnAction(e -> {
                selectedMonster = monster;
                newStage.close();
            });
            return temp;
        }).forEachOrdered((temp) -> {
            inner.getChildren().add(temp);
        });
        ScrollPane pane = new ScrollPane(inner);
        pane.setMinWidth(250);
        pane.setMinHeight(400);
        Button cancel = new Button("Cancel");cancel.setCancelButton(true);
        cancel.setOnAction(e -> {
            selectedMonster = null;
            newStage.close();
        });
        ToolBar toolbar = new ToolBar(cancel);
        Scene scene = new Scene(new VBox(pane, toolbar));
        scene.getStylesheets().add(this.getClass().getResource("NiceEncounter.css").toExternalForm());
        newStage.setScene(scene);
        newStage.showAndWait();
        return selectedMonster;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
