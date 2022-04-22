package com.source.controller;

import com.source.pojo.Person;
import com.source.utils.HttpUtil;
import com.source.utils.HttpUtils;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Source
 * @date 2022/04/04/22:52
 */
public class MainController {
    private static String qq;

    private static Image image;

    private static Stage stages;

    @FXML
    public TextField csrf;
    @FXML
    public TextField num;
    @FXML
    public TextField cookies;
    @FXML
    public TextField delay;
    @FXML
    public TextField startDates;

    ThreadPoolExecutor threadPoolExecutor;

    @FXML
    ImageView avatar;

    @FXML
    Label account;

    @FXML
    private TableView<Person> tb;

    @FXML
    private TableColumn<Person, Integer> tbId;

    @FXML
    private TableColumn<Person, String> tbName;

    @FXML
    private TableColumn<Person, String> tbStock;

    @FXML
    private TableColumn<Person, String> tbAward;

    @FXML
    private TableColumn<Person, String> tbStatus;

    @FXML
    public TextArea logs;

    @FXML
    public Button start;

    public static ArrayList<Person> init = new ArrayList<>();

    public void initialize(Stage stage) {

        stage.setOnCloseRequest(event -> {
            // 取消任务
            // 关闭线程池
            List<Runnable> runnables = threadPoolExecutor.shutdownNow();
            runnables.forEach(System.out::println);
            threadPoolExecutor.shutdown();
//                boolean shutdown = threadPoolExecutor.isShutdown();
//                System.out.println(shutdown);
            threadPoolExecutor = null;
//                System.out.println(111);
//                stage.setScene(null);
            Platform.exit();
        });

//        try {
//            service.bilBil();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
        init.add(new Person(1, "B站", "投稿10QB", "10", null, "833", "4622", "971986"));
        init.add(new Person(2, "B站", "投稿30QB", "10", null, "834", "4623", "971987"));
        init.add(new Person(3, "B站", "投稿60QB", "10", null, "835", "4624", "971985"));
        init.add(new Person(4, "B站", "投稿100QB", "10", null, "836", "4625", "972726"));
        init.add(new Person(5, "B站", "观看直播10QB", "10", null, "843", "4631", "0"));
        init.add(new Person(6, "B站", "每日直播10QB", "10", null, "844", "4632", "0"));
        init.add(new Person(7, "B站", "直播100QB", "10", null, "846", "4634", "0"));

        threadPoolExecutor = new ThreadPoolExecutor(
                20,
                30,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化常量
        qq = "1134496928";
        stages = stage;

        // 初始化场景
        InputStream downloadImage = HttpUtil.doDownloadAvatar(qq);
        assert downloadImage != null;
        image = new Image(downloadImage);
        stages.setTitle(qq);
        stages.getIcons().add(image);
        addText("默认选择->" + init.get(0).getAward());

        // 初始化数据
        avatar.setImage(image);
        account.setText(qq);
        account.setPrefWidth(image.getWidth());
        logs.appendText("执行日志：\n");
        logs.setWrapText(true);
        logs.setEditable(false);

        System.out.println(image.getWidth() + 10);
        // 初始化表格数据
        tb.setLayoutX(image.getWidth() + 10);
        tb.setPrefHeight(image.getHeight() + 25);

        ObservableList<Person> list = FXCollections.observableArrayList();
        tbId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        tbName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tbAward.setCellValueFactory(new PropertyValueFactory<>("Award"));
        tbStock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        tbStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));

        // 是否支持多选
        tb.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tb.setOnMouseClicked(event -> {
            int focusedIndex = tb.getSelectionModel().getFocusedIndex();
            addText("选择了->" + init.get(focusedIndex).getAward());
        });
        tb.setPlaceholder(new Label("暂无"));
        tb.setTableMenuButtonVisible(true);
//        tb.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        // 禁用排序
        ObservableList<TableColumn<Person, ?>> columns = tb.getColumns();
        columns.forEach(s -> {
            s.setSortable(false);
        });

        num.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[a-zA-Z]")) {
                return null;
            }
            return change;
        }));

        tb.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tb.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        list.addAll(init);

//        list.add(new Person(1, "B站", "投稿10QB", "10", null, "833", "4622", "971986"));

        tb.setItems(list);
    }

    @FXML
    public void onAvatar() {
        Stage stage = new Stage();
        stage.setTitle("新窗口");
        stage.setWidth(300);
        stage.setHeight(200);
        TextField field = new TextField();
        field.setAlignment(Pos.CENTER);
        field.setOnDragOver(event -> event.acceptTransferModes(TransferMode.ANY));
        field.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                String path = dragboard.getFiles().get(0).getAbsolutePath();
                field.setText(path);
            }
        });
        Button button = new Button("确认");
        button.setOnMouseClicked(event -> {
            Thread thread = new Thread(() -> {
                Platform.runLater(() -> {
                    qq = field.getText();
                    InputStream downloadImage1 = HttpUtil.doDownloadAvatar(qq);
                    assert downloadImage1 != null;
                    image = new Image(downloadImage1);
                    avatar.setImage(image);
                    account.setText(qq);
                });
            });
            thread.start();
        });
        AnchorPane b = new AnchorPane();
        b.getChildren().add(new Label("新窗口"));
        b.getChildren().add(field);
        b.getChildren().add(button);
        Scene scene1 = new Scene(b, 200, 100);
        stage.setScene(scene1);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(stages);
        stage.show();
    }

    void addText(String text) {
        Platform.runLater(() -> {
            logs.appendText(text);
            logs.appendText("\r\n");
        });
    }

    @FXML
    void onStart() {
        if (num.getText() == null || Objects.equals(num.getText(), "")) {
            num.setText("1");
        }
        if (delay.getText() == null || Objects.equals(delay.getText(), "")) {
            delay.setText("1");
        }
//        tb.getSelectionModel().select(1);
//        logs.clear();
        long start = System.currentTimeMillis();
        threadPoolExecutor.submit(() -> {
            long startDate = 0L;
            long l1 = System.currentTimeMillis() - System.currentTimeMillis() % 3600000 + 3600000;
            long l = (60 - Integer.parseInt(startDates.getText())) * 1000L;
            long l2 = l1 - l;
            startDate = l2;

            long now = System.currentTimeMillis();
            if (now + 5000 < startDate) {
                System.out.println(((System.currentTimeMillis() - System.currentTimeMillis() % 3600000) + 3600000) - ((60 - Integer.parseInt(startDates.getText())) * 1000L));
                System.out.println(startDate);
                System.out.println(System.currentTimeMillis());
                System.out.println("还未到秒杀开始时间，等待中......");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                addText("定时:" + dateFormat.format(l2));
                try {
                    Thread.sleep(startDate - now - 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("次数：" + Integer.valueOf(num.getText()));
            for (int i = 0; i < Integer.parseInt(num.getText()); i++) {
                threadPoolExecutor.execute(() -> {
                    System.out.println("----------------" + Thread.currentThread().getName());
                    try {
                        Bili();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("time:" + (end - start) + "ms");
//        addText("---" + Thread.currentThread().getName());
//        System.out.println("---" + Thread.currentThread().getName());
//        thread.start();
        int focusedIndex = tb.getSelectionModel().getFocusedIndex();
        System.out.println("当前选择->" + init.get(focusedIndex).toString());
//        start = 0L;
//        end = 0L;
    }

    public void Bili() throws ExecutionException, InterruptedException {
        System.out.println("2222222222222222" + Thread.currentThread().getName());
        int focusedIndex = tb.getSelectionModel().getFocusedIndex();
        Person person = init.get(focusedIndex);
//        System.out.println(Runtime.getRuntime().availableProcessors());
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {

            try {
                Thread.sleep(Long.parseLong(delay.getText()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HashMap<String, Object> httpHeaders = new HashMap<>(16);
//            httpHeaders.put("csrf", "3012e127444b46a3d334b2693ef3e4e7");
            httpHeaders.put("csrf", csrf.getText());
            httpHeaders.put("act_id", "264");
            httpHeaders.put("task_id", person.getTask_id());
            httpHeaders.put("group_id", person.getGroup_id());
            httpHeaders.put("receive_id", person.getReceive_id());
            httpHeaders.put("receive_from", "missionLandingPage");

            String cookie = "buvid3=50FB043E-7B87-36E0-69B0-E031A5933F7073631infoc; rpdid=|(k||Rl)JYJm0J'uYR)m)ul)u; fingerprint=702234b250bda4057914eef43e77f42b; buvid_fp=50FB043E-7B87-36E0-69B0-E031A5933F7073631infoc; buvid_fp_plain=undefined; SESSDATA=828c5573%2C1664533614%2C5aa73%2A41; bili_jct=3012e127444b46a3d334b2693ef3e4e7; DedeUserID=95747898; DedeUserID__ckMd5=731026ea9b5c990f; sid=70j7knex; CURRENT_BLACKGAP=0; blackside_state=0; CURRENT_QUALITY=116; i-wanna-go-back=-1; b_ut=5; nostalgia_conf=-1; bp_video_offset_95747898=645269207955537900; CURRENT_FNVAL=4048; LIVE_BUVID=AUTO2716491377265894; innersign=1";

            try {
                HttpUtil.ResEntity resEntity = HttpUtils.doPost("https://api.bilibili.com/x/activity/mission/task/reward/receive", null, cookies.getText(), httpHeaders);
                addText(person.getAward() + "---" + resEntity.getResponse() + "---" + Thread.currentThread().getName());
//                    addText("---" + 1);
                System.out.println("---" + Thread.currentThread().getName());
//                    addText(person.getAward() + "---" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 业务
        }, threadPoolExecutor);
        // 等待执行完成
        CompletableFuture.anyOf(voidCompletableFuture).get();
    }

    /**
     * 将时间字符串转换为时间戳
     *
     * @param dateStr yyyy-mm-dd格式
     * @return
     */
    public long convertDateToInt(String dateStr) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(dateStr);
        return date.getTime();
    }

//    public static void main(String[] args) {
//        HashMap<String, Object> httpHeaders = new HashMap<>(16);
//        httpHeaders.put("csrf", "1958ba40c43f3ef530498b8c7b8c39fc");
//        httpHeaders.put("act_id", "264");
//        httpHeaders.put("task_id", "836");
//        httpHeaders.put("group_id", "4625");
//        httpHeaders.put("receive_id", "972726");
//        httpHeaders.put("receive_from", "missionLandingPage");
//
//        HashMap<String, Object> map = new HashMap<>(16);
//
//        map.put("referer", "https://www.bilibili.com/");
//
//        String cookie = "sid=jn21hko2; fingerprint=08c0f9d31589214648771123407fbabe; DedeUserID=95747898; DedeUserID__ckMd5=731026ea9b5c990f; SESSDATA=ea345c41%2C1654047442%2C699f6*c1; bili_jct=1958ba40c43f3ef530498b8c7b8c39fc; video_page_version=v_old_home; rpdid=|(JY)kmlkJu|0J'uYJ)Rmllu~; buvid3=D8AB867D-F604-2CE5-888E-55E4F65539AC89461infoc; CURRENT_BLACKGAP=0; i-wanna-go-back=-1; b_ut=5; LIVE_BUVID=AUTO5416457619843769; _uuid=C641C176-664D-34B9-E929-81079EDDADF6A42381infoc; buvid_fp=2aedf8162804f76f934dd0bf434130b2; buvid4=7A31D4B1-96EF-DB43-6028-6554BEB64A1143304-022031223-XM+G1Gay5PCjZ7/yc/5MwA%3D%3D; bp_video_offset_95747898=637717637555552400; nostalgia_conf=-1; blackside_state=0; CURRENT_FNVAL=4048; CURRENT_QUALITY=120";
//        try {
//            for (int i = 0; i <= 1; i++) {
//                HttpUtil.ResEntity resEntity = HttpUtils.doPost("https://api.bilibili.com/x/activity/mission/task/reward/receive", null, cookie, httpHeaders);
//                System.out.println(resEntity);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {

        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
                20,
                30,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50),
                new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 100; i++) {
            threadPoolExecutor.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }

}
