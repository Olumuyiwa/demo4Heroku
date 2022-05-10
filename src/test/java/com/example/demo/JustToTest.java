package com.example.demo;


import com.boohoo.esbdefused.businessalerter.client.BusinessAlert;
import com.boohoo.esbdefused.canonical.PurchaseOrder;
import com.boohoo.esbdefused.test.tools.*;
import com.example.demo.utils.Thrower;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jr.ob.JSON;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.awaitility.Awaitility;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@Slf4j
public class JustToTest {


    String INPUT_LOCATION =                   "C:\\my-folder\\src-code\\play\\java\\boohoo\\acceptance-tests\\src\\test\\resources\\com\\boohoo\\esbdefused\\test\\acceptance\\purchaseorderprocessor\\inputs\\ordering-app\\";
    String OUTPUT_LOCATION =                  "C:\\my-folder\\src-code\\play\\java\\boohoo\\acceptance-tests\\src\\test\\resources\\com\\boohoo\\esbdefused\\test\\acceptance\\purchaseorderprocessor\\expected-outputs\\ordering-app\\";
    String OUTPUTS_ACCOUNTING_RECEIPTS_DIR =  "C:\\my-folder\\src-code\\play\\java\\boohoo\\acceptance-tests\\src\\test\\resources\\com\\boohoo\\esbdefused\\test\\acceptance\\purchaseorderprocessor\\expected-outputs\\accounting-receipts\\";
    String INPUTS_ACCOUNTING_PO_DIR = INPUT + "accounting-purchase-orders\\";
    private static final String PACKAGE_DIR = "C:\\my-folder\\src-code\\play\\java\\boohoo\\acceptance-tests\\src\\test\\resources\\com\\boohoo\\esbdefused\\test\\acceptance\\purchaseorderprocessor\\";
    private static final String INPUT = PACKAGE_DIR + "inputs\\";
    ObjectMapper objectMapper = createObjectMapper();
    public ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
    public String getContentOfFile( String fileApth) throws Exception{
        File file = new File(fileApth);
        return FileUtils.readFileToString(file, "UTF-8");

    }
    private void compareFilesWithQueueMessages(String queueName, List<String> purchaseOrderIds, String outputDirectory) throws Exception {
        List<String> actualMessages = SqsUtils.getMessages(queueName, purchaseOrderIds.size(), 15);
        Collections.sort(actualMessages);
        System.out.println("purchaseOrderIds.size()- = " + purchaseOrderIds.size());
        System.out.println("actualMessages.size()- = " + actualMessages.size());
        System.out.println("queueName = " + queueName);


        Iterator<String> actualMessagesIterator = actualMessages.iterator();

        while (actualMessagesIterator.hasNext()) {
            for (String purchaseOrderId : purchaseOrderIds) {
                String actual = actualMessagesIterator.next();
                System.out.println("outputDirectory + purchaseOrderId = " + outputDirectory + purchaseOrderId + ".json");
                String expected =getContentOfFile(outputDirectory + purchaseOrderId + ".json");
                System.out.println("========================");
                System.out.println("actual = " + actual);
                System.out.println("expected = " + expected);
                System.out.println("========================");
                JSONAssert.assertEquals(expected, actual, false);
            }
        }
    }
    private void compareFilesWithQueueMessages2(String queueName, List<String> purchaseOrderIds, String outputDirectory) throws Exception {
        List<String> actualMessages = SqsUtils.getMessages(queueName, purchaseOrderIds.size(), 15);
        Collections.sort(actualMessages);
        System.out.println("purchaseOrderIds.size()- = " + purchaseOrderIds.size());
        System.out.println("actualMessages.size()- = " + actualMessages.size());
        System.out.println("queueName = " + queueName);


        Iterator<String> actualMessagesIterator = actualMessages.iterator();

        while (actualMessagesIterator.hasNext()) {
            for (String purchaseOrderId : purchaseOrderIds) {
                String actual = actualMessagesIterator.next();
                System.out.println("outputDirectory + purchaseOrderId = " + outputDirectory + purchaseOrderId + ".json");
                String expected =getContentOfFile(outputDirectory + purchaseOrderId + ".json");
                System.out.println("========================");
                System.out.println("actual = " + actual);
                System.out.println("expected = " + expected);
                System.out.println("========================");
                JSONAssert.assertEquals(expected, actual, false);
            }
        }
    }
    /*
    * =========================================== fine ===================================
When a purchase order is received from accounting with an invalid buyer name 	# StepDefs.a_purchase_order_is_received_from_accounting_with_an_invalid_buyer_name()
Then the purchase order should not be sent to po service                        # StepDefs.the_purchase_order_should_not_be_sent_to_po_service()
-------------------------------------not fine -------------------------------------
And a business alert with subject: "PO containing invalid Buyer Name Alert" should be triggered with the following message # StepDefs.the_alert_should_contain_the_following_message(String,String)
*/
    void purchase_order_received_from_accounting_with_an_invalid_buyer_name() throws Exception {
        beforeScenario();
        String inputFile = INPUT + "accounting-purchase-orders\\PreAdvice-invalid-buyer-name.xml";
        InputStream inputStream = new FileInputStream(new File(inputFile));
        String inputFileNameOnlyAsTargetFilename = "PreAdvice-invalid-buyer-name.xml";

        try {
            GenericKeyedObjectPool<Pair<String, String>, ChannelSftp> sftpObjectPool;

            SftpObjectPoolFactory sftpObjectPoolFactory = new SftpObjectPoolFactory(Env.sftpHost());
            sftpObjectPool = new GenericKeyedObjectPool<Pair<String, String>, ChannelSftp>(sftpObjectPoolFactory);
            sftpObjectPool.setTestOnBorrow(true);
            SftpClient.configure(sftpObjectPool);

            SftpClient sftpClient = new SftpClient("accounting", "accounting");
            System.out.println("sftpClient = " + sftpClient);
            String filename = "in/" + inputFileNameOnlyAsTargetFilename;
            sftpClient.storeFile(filename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }

        String PO_SERVICE_IN = "dev-israel-" + "purchase-order-service";
        System.out.println("SqsUtils.getMessages(PO_SERVICE_IN, 1, 5).isEmpty() = " + SqsUtils.getMessages(PO_SERVICE_IN, 1, 5).isEmpty());
        // assertTrue(SqsUtils.getMessages(PO_SERVICE_IN, 1, 5).isEmpty());
        Awaitility.await().atMost(90, TimeUnit.SECONDS).untilAsserted(() -> {
            String actualJson = SqsUtils.getFirstMessage(QueueNames.BUSINESS_ALERTS, 10);
            assertNotNull("Business alert message not found", actualJson);

            BusinessAlert actual = JSON.std.beanFrom(BusinessAlert.class, actualJson);
//           System.out.println("actualAlert = " + actualAlert);
//           assertEquals(expectedSubject, actual.getSubject());
//           assertEquals("Business alert not as expected", expectedMessage, actual.getMessage());
        });

    }
    //failedtest1
    void rebuyPurchase0rderCreationWithAstylethatdoesnotexist()
            throws Exception {
        String inputFilePath = "purchase-order-pim-rebuy-style-doesnt-exist.json";
        String outputFilePath ="purchase-order-pim-rebuy-style-doesnt-exist-receipt";
        inputFilePath = INPUT_LOCATION + inputFilePath;

        InputStream targetStream = new FileInputStream(new File( inputFilePath));
        String jsonOfInput =getContentOfFile(inputFilePath);
        log.info("jsonOfInput = {}",jsonOfInput);
// @When("^a purchase order \"(.*?)\" is received from ordering app$")
        SqsUtils.sendMessage(IOUtils.toString(targetStream, StandardCharsets.UTF_8), QueueNames.CREATE_PO_FROM_BUYER_APP);
        log.info("SqsUtils.sendMessage json to  QueueNames.CREATE_PO_FROM_BUYER_APP - " + QueueNames.CREATE_PO_FROM_BUYER_APP);
        log.info("the_purchase_order_exists_receipt_should_be_sent_to_ordering_app_queue ");


// @Then("^the rebuy purchase order should not be sent to accounting queue$")
        SqsUtils.countMessages(QueueNames.PO_PIM_PO_TO_ACCOUNTING, 0, 10);
        log.info("compareFilesWithQueueMessages" +
                "|QueueNames.OUTPUTS_ACCOUNTING_RECEIPTS_DIR =  "
                + QueueNames.SEND_PO_CREATION_RECEIPT_TO_BUYER_APP
                +" | OUTPUTS_ACCOUNTING_RECEIPTS_DIR - "
                +OUTPUTS_ACCOUNTING_RECEIPTS_DIR );
        String fileName ="purchase-order-pim-rebuy-style-doesnt-exist-receipt";
        compareFilesWithQueueMessages(QueueNames.SEND_PO_CREATION_RECEIPT_TO_BUYER_APP,
                Collections.singletonList(fileName), OUTPUTS_ACCOUNTING_RECEIPTS_DIR);

    }
    void testpurchase_order_012345()throws Exception {
        String inputFilePath = "purchase-order-012345.json";
        String outputFilePath = ("purchase-order-012345.json");
        inputFilePath = inputFilePath.replaceAll("\n","");
        ObjectMapper objectMapper = createObjectMapper();
        String jsonOfInput =getContentOfFile(inputFilePath);
        String jsonOfExpectedOutput =getContentOfFile(outputFilePath);
        com.boohoo.esbdefused.canonical.orderingapplication.PurchaseOrder expected =
                objectMapper.readValue(jsonOfExpectedOutput, com.boohoo.esbdefused.canonical.orderingapplication.PurchaseOrder.class);
        System.out.println("input string = " + jsonOfInput);
        InputStream targetStream = new FileInputStream(new File( inputFilePath));
        SqsUtils.sendMessage(IOUtils.toString(targetStream, StandardCharsets.UTF_8),
                QueueNames.CREATE_PO_FROM_BUYER_APP);
        String queueName = QueueNames.SEND_PO_TO_ACCOUNTING;
        List<String> messages = SqsUtils.getMessages(queueName, 1, 15);
        int countofMsg = messages.size();
        String actualMessage = messages.get(0);
        com.boohoo.esbdefused.canonical.orderingapplication.PurchaseOrder actualObject =
                objectMapper.readValue(actualMessage, com.boohoo.esbdefused.canonical.orderingapplication.PurchaseOrder.class);
        System.out.println("expectedPurchaseOrder - " +expected);
        System.out.println("actualPurchaseOrder - " +actualObject);
        Assertions.assertEquals(expected, actualObject);
    }
    void test_purchase_order_pim_existing_product() throws Exception {
// When a purchase order "purchase-order-pim-existing-product" is received from ordering app
// StepDefs.a_purchase_order_is_received_from_ordering_app(String)
        String myQueuePrifix = "dev-israel";
        String uatQueuePrifix = "uat";
        String fileName = "purchase-order-pim-existing-product";
        String OUTPUTS_ORDERINGAPP_PO_DIR = OUTPUT_LOCATION;
        String inputFilePath = INPUT_LOCATION+ ("purchase-order-pim-existing-product.json");
        String jsonOfInput = getContentOfFile(inputFilePath);
        System.out.println("input string = " + jsonOfInput);
        String outputFilePath = OUTPUT_LOCATION + ("purchase-order-pim-existing-product");
        InputStream targetStream = new FileInputStream(new File( inputFilePath));
        SqsUtils.sendMessage(IOUtils.toString(targetStream, StandardCharsets.UTF_8),QueueNames.CREATE_PO_FROM_BUYER_APP);

        System.out.println("inside the_rebuy_purchase_order_should_be_sent_to_accounting_queue " + fileName);
        System.out.println("fileName = " + fileName);
        System.out.println("QueueNames.PO_PIM_PO_TO_ACCOUNTING " + QueueNames.PO_PIM_PO_TO_ACCOUNTING);

        compareFilesWithQueueMessages(QueueNames.PO_PIM_PO_TO_ACCOUNTING, Collections.singletonList(fileName), OUTPUTS_ORDERINGAPP_PO_DIR);
    }
    public static void main(String[] args) throws  Exception {
        JustToTest justToTest = new JustToTest();
//        justToTest.testpurchase_order_012345();
        justToTest.rebuyPurchase0rderCreationWithAstylethatdoesnotexist();
//        justToTest.purchase_order_received_from_accounting_with_an_invalid_buyer_name();
    }

    public void beforeScenario() throws Exception {
        log.info("Setting up for scenario");

        ExecutorService exec = Executors.newCachedThreadPool();

        List<Callable<Void>> tasks = new ArrayList<>();
//        tasks.add(callable(() -> SageUtils.cleanExportDirectory()));
        tasks.add(callable(esbDatabase::setup));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.BUSINESS_ALERTS)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.CREATE_PO_FROM_BUYER_APP)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ORDERING_APP_TO_WHOLESALEABLE)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.SEND_PO_TO_ACCOUNTING)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PO_PIM_PO_TO_ACCOUNTING)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PO_CREATION_RECEIPT_FROM_ACCOUNTING)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.SEND_PO_CREATION_RECEIPT_TO_BUYER_APP)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PURCHASE_ORDERS)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PURCHASE_ORDERS_DLQ)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PURCHASE_ORDER_TO_WAREHOUSE)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PURCHASE_ORDER_TO_WAREHOUSE_DLQ)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PO_SERVICE_IN)));
        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PO_SERVICE_DLQ)));
        tasks.add(callable(() -> exchangesSentToPurchaseOrderProcessor = getExchangesTotal(PO_PROCESSOR_ROUTE_ID)));

        exec.invokeAll(tasks).stream().forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        exec.shutdownNow();
        log.info("Finished setting up for scenario");
    }
    private Callable<Void> callable(Thrower fun) {
        return () -> {
            try {
                fun.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return null;
        };
    }
    private int getExchangesTotal(String routeId) throws IOException {
        return CamelRouteMetricsUtils.getRouteExchangesTotal(routeId, 8098);
    }
    private int exchangesSentToPurchaseOrderProcessor = 0;
    private final EsbDatabase esbDatabase = new EsbDatabase();
    private static final String PO_PROCESSOR_ROUTE_ID = "process-purchase-order-from-accounting";
}