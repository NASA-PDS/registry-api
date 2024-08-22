package gov.nasa.pds.api.registry.configuration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogic;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;

class WebMVCConfigTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {


  }

  @Test
  void selectFormatterClassFromSingleFormatSuccessfulTest() {

    String format = "text/html";
    String expectedFormatterClassName =
        "gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject";
    String foundFormatterClassName;

    try {
      Class<? extends ProductBusinessLogic> formatter = WebMVCConfig.selectFormatterClass(format);

      foundFormatterClassName = formatter.getName();
      assertEquals(expectedFormatterClassName, foundFormatterClassName);

    } catch (AcceptFormatNotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  void selectFormatterClassFromSingleFormatFailedTest() {

    String format = "text/htmm";

    Exception exception = assertThrows(AcceptFormatNotSupportedException.class, () -> {
      WebMVCConfig.selectFormatterClass(format);
    });

    String expectedMessage = "None of the format(s) text/htmm is supported.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }

  @Test
  void selectFormatterClassFromMultipleFormatSuccessfulTest() {

    String format = "text/ms+word,text/html";
    String expectedFormatterClassName =
        "gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject";
    String foundFormatterClassName;

    try {
      Class<? extends ProductBusinessLogic> formatter = WebMVCConfig.selectFormatterClass(format);

      foundFormatterClassName = formatter.getName();
      assertEquals(expectedFormatterClassName, foundFormatterClassName);

    } catch (AcceptFormatNotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  void selectFormatterClassFromMultipleFormatExtraSpacesSuccessfulTest() {

    String format = "text/ms+word,text/html ,anything/something";
    String expectedFormatterClassName =
        "gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject";
    String foundFormatterClassName;

    try {
      Class<? extends ProductBusinessLogic> formatter = WebMVCConfig.selectFormatterClass(format);

      foundFormatterClassName = formatter.getName();
      assertEquals(expectedFormatterClassName, foundFormatterClassName);

    } catch (AcceptFormatNotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  void selectFormatterClassFromMultipleFormatFailedTest() {

    String format = "text/htmm,car/porsche+911";

    Exception exception = assertThrows(AcceptFormatNotSupportedException.class, () -> {
      WebMVCConfig.selectFormatterClass(format);
    });


    String expectedMessage = "None of the format(s) text/htmm,car/porsche+911 is supported.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }


}
