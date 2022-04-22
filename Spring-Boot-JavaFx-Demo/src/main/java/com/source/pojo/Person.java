package com.source.pojo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Source
 * @date 2022/04/05/14:34
 */
public class Person {

    private Integer id;

    private String name;

    private String Stock;

    private String Award;

    private String status;

    private String task_id;

    private String group_id;

    private String receive_id;

    public Person(Integer id, String name, String stock, String award, String status) {
        this.id = id;
        this.name = name;
        Stock = stock;
        Award = award;
        this.status = status;
    }

    public Person(Integer id, String name, String award, String stock, String status, String task_id, String group_id, String receive_id) {
        this.id = id;
        this.name = name;
        Award = award;
        Stock = stock;
        this.status = status;
        this.task_id = task_id;
        this.group_id = group_id;
        this.receive_id = receive_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public String getAward() {
        return Award;
    }

    public void setAward(String award) {
        Award = award;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", Stock='" + Stock + '\'' +
                ", Award='" + Award + '\'' +
                ", status='" + status + '\'' +
                ", task_id='" + task_id + '\'' +
                ", group_id='" + group_id + '\'' +
                ", receive_id='" + receive_id + '\'' +
                '}';
    }
}
