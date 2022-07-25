package com.example.demo.mygeneraltest;

import com.boohoo.esbdefused.test.tools.ClasspathUtils;
import com.boohoo.esbdefused.test.tools.QueueNames;
import com.boohoo.esbdefused.test.tools.SqsUtils;
import com.example.demo.AkeneoStubClientRewrite;
import com.example.demo.JustToTest;
import com.example.demo.utils.Thrower;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class MyStepdefs {

    private static final String INPUT_DIR = "input\\";
    private static final String OUTPUT_DIR = "output\\";
    private static final String PACKAGE_DIR
            = "C:\\my-folder\\src-code\\play\\java\\personal\\acceptance-test-personal-version\\src\\test\\resources\\com\\demo\\mygeneraltest\\";
    private final AkeneoStubClientRewrite akeneoStubClient = new AkeneoStubClientRewrite();
//    @Before
//    public void beforeScenario() throws Exception {
//        log.info("Preparing for scenario");
//
//        ExecutorService exec = Executors.newCachedThreadPool();
//        List<Callable<Void>> tasks = new ArrayList<>();
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_INFORMATION_MANAGEMENT_PROCESSOR_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_INFORMATION_MANAGEMENT_PROCESSOR_DLQ)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_REFERENCE_UPDATE_TRIGGER_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_REFERENCE_UPDATE_TRIGGER_DLQ)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ENRICHED_PRODUCT_INFORMATION_BUYING_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ENRICHED_PRODUCT_INFORMATION_SALES_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ENRICHED_PRODUCT_INFORMATION_WAREHOUSE_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.COMMODITY_CODE_FROM_CRITICAL_PATH_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ENRICHED_PRODUCT_INFORMATION_BUYING_ACCOUNTING_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.ENRICHED_PRODUCT_ACCOUNTING_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.COMMODITY_CODE_TO_ACCOUNTING_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.STYLETRACK_PRODUCT_UPDATE_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.STYLETRACK_PRODUCT_UPDATE_DLQ)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.CUBISCAN_PRODUCT_DIMENSIONS_UPDATE_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.CUBISCAN_PRODUCT_DIMENSIONS_UPDATE_DLQ)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_FOR_TRANSLATION_QUEUE)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_FOR_TRANSLATION_DLQ)));
//        tasks.add(callable(() -> SqsUtils.purgeQueue(QueueNames.PRODUCT_FOR_TRANSLATION_UPDATE_DLQ)));
////        tasks.add(callable(pimDatabase::setup));
//
//        exec.invokeAll(tasks).forEach(future -> {
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        log.info("Finished preparing for scenario");
//    }

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

    //Given that cubiscan product request to update "product-dimensions" for a list of skus
//    @Given("^that cubiscan product update is sent for measured sku \"(.*?)\"$")
    @Given("^that cubiscan product request is sent to update sku sizes \"(.*?)\"$")
    public void cubiscan_product_request_to_update_sku_sizes(String productDimension) throws IOException {
//        String json = ClasspathUtils.getClasspathResourceAsUtf8String(PACKAGE_DIR + INPUT_DIR + "cubiscan_product_update_" + sku + ".json");
        System.out.println("filepath = " + PACKAGE_DIR + INPUT_DIR + "cubiscan_" + productDimension + ".json");
        String json = JustToTest.getContentOfFile(PACKAGE_DIR + INPUT_DIR + "cubiscan_" + productDimension + ".json");
        SqsUtils.sendMessage(json, QueueNames.CUBISCAN_PRODUCT_DIMENSIONS_UPDATE_QUEUE);
    }

    @Then("^the \"(.*?)\" update call to Akeneo should be made for the following skus$")
    public void the_product_dimensions_update_to_Akeneo_should_be_made_for_the_following(String productDimensions, DataTable items) throws IOException {
//        final String expectedJson = ClasspathUtils.getClasspathResourceAsUtf8String(PACKAGE_DIR + OUTPUT_DIR + "product_update_ABB01456-148-287.json");
        System.out.println("filepath = " + PACKAGE_DIR + OUTPUT_DIR + "product_update_" + productDimensions + ".json");
        String expectedJson = JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR + "product_update_" + productDimensions + ".json");
        List<String> skus = items.asLists(String.class).get(0);

        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {

            for (String sku : skus) {
                System.out.println("sku = " + sku);
                final String actualJson = akeneoStubClient.getPatchedProduct(sku);
                assertEquals(expectedJson, actualJson);
            }
        });
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
//            final String actualJson = akeneoStubClient.getPatchedProduct(sku);
//            System.out.println("actualJson = " + actualJson);
//            assertEquals(expectedJson, actualJson);
        });
    }

    @Then("^the cubiscan dead letter queue should not contain any messages$")
    public void the_cubiscan_dead_letter_queue_should_not_contain_messages() {
        assertEquals(0, SqsUtils.countMessages(QueueNames.CUBISCAN_PRODUCT_DIMENSIONS_UPDATE_DLQ, 0, 20));
    }

    /*      Then the product update for sku "ABB01456-148-287" should be sent to Akeneo
            Then the product update call to Akeneo should be made for the following
    */
    @Then("^the product update for sku \"(.*?)\" should be sent to Akeneo$")
    public void the_product_update_for_sku_should_be_sent_to_akeneo(List<String> skuVariants) throws IOException {
        final String expectedJson = JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR + "product_update_" + skuVariants + ".json");
        System.out.println("list of skuVariants = " + skuVariants);
        String styleCode = "DZZ12345";
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
            final String actualJson = akeneoStubClient.getPatchedProduct(styleCode);
            System.out.println("expectedJson = " + expectedJson);
            System.out.println("actualJson = " + actualJson);
           assertEquals(expectedJson, actualJson);

        });
    }

    @When("^a purchase order \"([^\"]*)\" is received from ordering app$")
    public void aPurchaseOrderIsReceivedFromOrderingApp(String fileName) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        String json = JustToTest.getContentOfFile(PACKAGE_DIR + INPUT_DIR  + fileName + ".json");
        SqsUtils.sendMessage(json, QueueNames.CREATE_PO_FROM_BUYER_APP);
        log.info("SqsUtils.sendMessage(json, QueueNames.CREATE_PO_FROM_BUYER_APP)");
    }

    @Then("^the asset \"(.*?)\" update call to Akeneo should be made$")
    public void the_product_asset_update_to_akeneo_should_happen(String fileName) throws Throwable {
        final String expectedJson =  JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR  + fileName + ".json");
        String styleCode = "DZZ12345";
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
            final String actualJson = akeneoStubClient.getPatchedAsset(styleCode);
            log.info("The response for style: '{}' is: '{}'",styleCode,actualJson);
            Assert.assertEquals(expectedJson, actualJson);
        });
    }
    @And("^the taxonomy \"(.*?)\" update call to Akeneo should be made$")
    public void the_product_model_taxonomy_update_to_akeneo_should_happen(String fileName) throws Throwable {
        final String expectedJson =  JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR  + fileName + ".json");
        String styleCode = "DZZ12345";
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
            final String actualJson = akeneoStubClient.getPatchedProductModel(styleCode);
            log.info("The response for style: '{}' is: '{}'",styleCode,actualJson);
            Assert.assertEquals(expectedJson, actualJson);
        });
    }

    @And("^the purchase order \"([^\"]*)\" should be sent to accounting queue$")
    public void thePurchaseOrderShouldBeSentToAccountingQueue(String arg0) throws Throwable {
        log.info("...About to do thePurchaseOrderShouldBeSentToAccountingQueue....");
    }

    @And("^the purchase order update \"([^\"]*)\"  should be sent to product update queue$")
    public void thePurchaseOrderUpdateShouldBeSentToProductUpdateQueue(String fileIdentifier) throws Throwable {
        String expectedEnrichedProductRecord =  JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR  + fileIdentifier + ".json");
                Awaitility.await().atMost(1, TimeUnit.MINUTES).pollDelay(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String actualEnrichedProductRecord = SqsUtils.getFirstMessage(QueueNames.PO_PIM_PRODUCT_PATCH, 20);
            JSONAssert.assertEquals(expectedEnrichedProductRecord, actualEnrichedProductRecord, false);
        });

//        String fileName = productId.contains("-") ? "enriched_product_record_" + productId : "enriched_product_with_storefront";
//        String expectedEnrichedProductRecord = ClasspathUtils.getClasspathResourceAsUtf8String(PACKAGE_DIR + OUTPUT_DIR + fileName + ".json");
//
//        Awaitility.await().atMost(1, TimeUnit.MINUTES).pollDelay(10, TimeUnit.SECONDS).untilAsserted(() -> {
//            String actualEnrichedProductRecord = SqsUtils.getFirstMessage(QueueNames.ENRICHED_PRODUCT_INFORMATION_BUYING_QUEUE, 20);
//            JSONAssert.assertEquals(expectedEnrichedProductRecord, actualEnrichedProductRecord, false);
//        });
    }

    @And("^the enriched purchase order \"([^\"]*)\" should be present in the product update queue$")
    public void theEnrichedPurchaseOrderShouldBePresentInTheProductUpdateQueue(String fileIdentifier) throws Throwable {
        String expectedEnrichedProductRecord =  JustToTest.getContentOfFile(PACKAGE_DIR + OUTPUT_DIR  + fileIdentifier + ".json");
        System.out.println("expectedEnrichedProductRecord  = " + expectedEnrichedProductRecord);

        Awaitility.await().atMost(1, TimeUnit.MINUTES).pollDelay(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String actualEnrichedProductRecord = SqsUtils.getFirstMessage(QueueNames.PO_PIM_PRODUCT_PATCH, 20);
            System.out.println("actualEnrichedProductRecord  = " + actualEnrichedProductRecord);
            JSONAssert.assertEquals(expectedEnrichedProductRecord, actualEnrichedProductRecord, false);
        });

    }

    @And("^there should be no messages in the product update dead letter queue$")
    public void thereShouldBeNoMessagesInTheProductUpdateDeadLetterQueue() {
        assertTrue(SqsUtils.getMessages(QueueNames.PRODUCT_FOR_TRANSLATION_DLQ, 1, 5).isEmpty());
    }
}
