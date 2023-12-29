package xml.expression.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLExpressionEvaluator extends Application {

    @FXML
    Label Title_res;
    
    @FXML
    Button button = new Button("Open a file");


    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader2 = new FXMLLoader(XMLExpressionEvaluator.class.getResource("/xml/expression/evaluator/Home.fxml"));
        Scene scene2 = null;
        try {
            scene2 = new Scene(fxmlLoader2.load());
        } catch (IOException ex) {
            Logger.getLogger(XMLExpressionEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        primaryStage.setTitle("Alexandria University");
        primaryStage.setScene(scene2);

        primaryStage.show();

        primaryStage.setOnCloseRequest((event) -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure you want to close this app?");
            alert.setContentText("Close App?");
            alert.setHeaderText("");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.exit(0);
            }
            event.consume();
        });
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static boolean isOperator(String x) {
        switch (x) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
            case "%":
                return true;
        }
        return false;
    }

    public static boolean isNumber(String x) {
        try {
            double d = Double.parseDouble(x);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Double result_Two_numbers(String c, double x, double y) {
        double res = 0;
        switch (c) {
            case "+":
                res = x + y;
                break;
            case "-":
                res = x - y;
                break;
            case "*":
                res = x * y;
                break;
            case "/":
                res = x / y;
                break;
            case "^":
                res = (long) Math.pow(x, y);
                break;
            case "%":
                res = x % y;
                break;
        }
        return res;
    }

    //+ * 3 + 5 4 * 9 8
    public static Stack<String> expression = new Stack();
    public static Stack<Double> result_Stack = new Stack();
    public static double result = 0;

    public static String Convert_TO_Infix(Stack stack) {
        if (stack.isEmpty()) {
            return "";
        }
        String operator = (String) stack.pop();
        if (operator != null) {
            if (isOperator(operator)) {
                String op1 = expression.pop();
                String op2 = expression.pop();
                double num1 = 0, num2 = 0;
                try {
                    num1 = result_Stack.pop();
                    num2 = result_Stack.pop();
                } catch (Exception e) { 
                    return "Not valid";
                }
                String temp = "(" + op1 + operator + op2 + ")";
                double temp2 = result_Two_numbers(operator, num1, num2);

                expression.push(temp, "");
                result_Stack.push(temp2, 0.0);
            } else {
                if (isNumber(operator)) {
                    expression.push(operator + "", "");
                    result_Stack.push(Double.valueOf(operator), 0.0);
                } else {
                    return "Not valid";
                }
            }
        }
        return Convert_TO_Infix(stack) + expression.pop();
    }

    public void Choose_file(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML file", "*.xml"));
        Stage stage = (Stage) button.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Title_res.setText("File has been selected");
            read_xml(selectedFile.getPath());
        } else {
            Title_res.setText("No file has been selected");
        }
    }

    public void read_xml(String path) {
        Stack<String> expression_Stack = new Stack<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(path));

            document.getDocumentElement().normalize();
            boolean ch = true;
            NodeList List = document.getElementsByTagName("expr");
            for (int i = 0; i < List.getLength(); i++) {
                Node EXPRs = List.item(i);
                if (EXPRs.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList Details = EXPRs.getChildNodes();
                    for (int j = 0; j < Details.getLength(); j++) {
                        Node detail = Details.item(j);
                        if (detail.getNodeType() == Node.ELEMENT_NODE) {
                            Element detailElement = (Element) detail;
                            if (detailElement.getTagName().equals("operator")
                                    || detailElement.getTagName().equals("atom") || detailElement.getTagName().equals("expr")) {
                                String valueE = detailElement.getAttribute("value");
                                if (detailElement.getTagName().equals("operator") && !isOperator(valueE)
                                        || detailElement.getTagName().equals("atom") && !isNumber(valueE)) {
                                    ch = false;
                                    break;
                                } else if (detailElement.getTagName().equals("expr")) {
                                    continue;
                                } else {
                                    expression_Stack.push(valueE, detailElement.getTagName());
                                }
                            } else {
                                ch = false;
                                break;
                            }
                        }
                    }
                    if (!ch) {
                        break;
                    }
                }
            }

            if (ch) {
                String infix = Convert_TO_Infix(expression_Stack);
                System.out.println(infix);
                if (infix.contains("Not valid") || result_Stack.size() != 1) {
                    Title_res.setText("Not valid expression");
                } else { 
                    Title_res.setText("Expression = "+infix+"\n\nResult = "+result_Stack.pop()); 
                }
            } else {
                 Title_res.setText("Not valid expression");
            }

        } catch (ParserConfigurationException | SAXException | IOException e) { 
                Title_res.setText("Xml isnt valid"); 
        }
    }
    
}

 
