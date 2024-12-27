
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetStoreApiTest {

    public static final String BASE_URL = "https://petstore.swagger.io/v2/pet/";
    public static final String PET_ID = "100";
    public static final String FILE_PATH = "C:\\Users\\allyg\\IdeaProjects\\PetStoreAPIProject\\src\\test\\files\\scottish fold.jpg.jpg";
    public static final String ADDITIONAL_META_DATA = "Scottish Fold";

    private static RequestSpecification reqSpec;
    public Integer drawnPetId;
    public String apiKey = "123";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;

    }

    @Test(priority = 1)
    public void uploadImageTest() {
        File file = new File(FILE_PATH);


        Response response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("file", file)
                .param("additionalMetaData", ADDITIONAL_META_DATA)
                .when()
                .post("/{petId}/uploadImage", PET_ID);

        response
                .then()
                .log().all()
                .statusCode(200);

                // Assertion of the response body
                String message = response.jsonPath().getString("message");
                Assert.assertTrue(message.contains("scottish fold"));

    }

     @Test(priority = 2)
     public void AddPetTest() {
         // Creating a pet object
         Map<String, Object> category = new HashMap<>();
         category.put("id", 10);
         category.put("name", "mice");

         List<Map<String, Object>> tagInfoList = new ArrayList<>();
         Map<String, Object> tagInfo= new HashMap<>();
         tagInfo.put("id", 99);
         tagInfo.put("name", "firsttag");
         tagInfoList.add(tagInfo);


         List<String> photoUrls = new ArrayList<>();
         //photoUrls.put("photoUrls","C:\\Users\\allyg\\IdeaProjects\\PetStoreAPIProject\\src\\test\\files\\fluffy.jpg" );
         photoUrls.add("https://static.vecteezy.com/system/resources/previews/030/681/712/large_2x/a-fluffy-creature-with-big-round-eyes-free-photo.jpg");


         Map<String, Object> pet = new HashMap<>();
         pet.put("id", 100);
         pet.put("category", category);
         pet.put("name", "alvin");
         pet.put("photoUrls",photoUrls);
         pet.put("tags", tagInfoList);
         pet.put("status", "available");

         System.out.println(pet);


         // Sending POST request to add the pet
         Response response = RestAssured
                 .given()
                 .contentType(ContentType.JSON)
                 .body(pet)
                 .when()
                 .post("https://petstore.swagger.io/v2/pet")
                 .then()
                 .log().all()
                 .statusCode(200)
                 .extract().response();

         drawnPetId= response.jsonPath().get("id");
         System.out.println("PET ID= "+ drawnPetId);

     }

     @Test(priority = 3)
         public void UpdatePetTest() {

         // Creating a pet object
         Map<String, Object> category = new HashMap<>();
         category.put("id", 10);
         category.put("name", "MICE");

         List<Map<String, Object>> tagInfoList = new ArrayList<>();
         Map<String, Object> tagInfo= new HashMap<>();
         tagInfo.put("id", 99);
         tagInfo.put("name", "UPDATEDTAG");
         tagInfoList.add(tagInfo);


         List<String> updatedPhotoUrls = new ArrayList<>();
         updatedPhotoUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkRKDwQ5u1Xnvv1BO1_T2uOzzV1u4Sbs156Q&s");

         Map<String, Object> pet = new HashMap<>();
         pet.put("id", 100);
         pet.put("category", category);
         pet.put("name", "ALVIN");
         pet.put("photoUrls",updatedPhotoUrls);
         pet.put("tags", tagInfoList);
         pet.put("status", "available");

         System.out.println(pet);


         // Sending POST request to add the pet
         Response response = RestAssured
                 .given()
                 .contentType(ContentType.JSON)
                 .body(pet)
                 .when()
                 .put(BASE_URL)
                 .then()
                 .log().all()
                 .statusCode(200)
                 .extract().response();

         String responseName = response.jsonPath().getString("name");
         Assert.assertEquals(responseName, "ALVIN");




     }
    @Test(priority = 4)
    public void findPetByIdTest() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{petId}",drawnPetId)
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();


        String actualPetName = response.jsonPath().getString("name");
        String actualPetCategory = response.jsonPath().getString("category");


        String expectedName = "ALVIN";
        String expectedCategory = "MICE";


        Assert.assertTrue(actualPetName.contains(expectedName), "Pet name does not contain expected value");
        Assert.assertTrue(actualPetCategory.contains(expectedCategory), "Pet category does not contain expected value");

        System.out.println("PETID= "+ drawnPetId);


    }

    @Test(priority = 5)
    public void updatePetWithFormDataTest() {

        String updatedNameExpected = "SINCAP";
        String updatedStatusExpected = "available";

        //creating formData Object
        HashMap<String,Object> formData = new HashMap<>();
        formData.put("name", updatedNameExpected);
        formData.put("status", updatedStatusExpected);


        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .formParams(formData)
                .when()
                .post("/{petId}", drawnPetId)
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        // Assertion of the response body
        String updatedNameActual=  response.jsonPath().getString("name");
        String updatedStatusActual= response.jsonPath().getString("status");


        Assert.assertTrue(updatedNameActual.contains(updatedNameExpected), "Pet name does not contain expected value");
        Assert.assertTrue(updatedStatusActual.contains(updatedStatusExpected), "Pet status does not contain expected value");

    }


    @Test(priority = 6)
    public void deletePetTest() {

        RestAssured
                .given()
                .header("api_key", apiKey)
                .when()
                .delete("/{petId}", drawnPetId)
                .then()
                .statusCode(200);

    }
}

