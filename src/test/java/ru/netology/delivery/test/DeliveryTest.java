package ru.netology.delivery.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    String planningDate = generateDate(4);

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").setValue(firstMeetingDate);
        $("[data-test-id=\"name\"] input").setValue(validUser.getName());
        $("[data-test-id=\"phone\"] input").setValue(validUser.getPhone());
        $("[data-test-id=\"agreement\"]").click();
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(), \"Успешно!\")]").should(visible, Duration.ofSeconds(3));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(3))
                .shouldBe(visible);
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").setValue(secondMeetingDate);
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(), \"Необходимо подтверждение\")]").should(visible, Duration.ofSeconds(3));
        $x("//*[contains(text(), \"У вас уже запланирована встреча на другую дату. Перепланировать?\")]").should(visible, Duration.ofSeconds(3));
        $x("//*[contains(text(), \"Перепланировать\")]").click();
        $x("//*[contains(text(), \"Успешно!\")]").should(visible, Duration.ofSeconds(3));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(3))
                .shouldBe(visible);
    }

    @Test
    void testFormWithoutCheckbox() {
        $("[data-test-id='city'] input").setValue("Челябинск");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Тиньков Олег");
        $("[data-test-id='phone'] input").setValue("+79997770011");
        $x("//span[@class=\"button__text\"]").click();
        $("label.input_invalid");
    }

    @Test
    void nameTest() {
        $("[data-test-id='city'] input").setValue("Челябинск");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Tinkov-Tinkoff Oleg");
        $("[data-test-id='phone'] input").setValue("+79997770011");
        $("[data-test-id='agreement']").click();
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(),'Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.')]");
    }

    @Test
    void cityTest() {
        $("[data-test-id='city'] input").setValue("Токио");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Тиньков Олег");
        $("[data-test-id='phone'] input").setValue("+79997770011");
        $("[data-test-id='agreement']").click();
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(),\"Доставка в выбранный город недоступна\")]");
    }

    @Test
    void dateTest() {
        planningDate = generateDate(1);
        $("[data-test-id='city'] input").setValue("Челябинск");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Тиньков Олег");
        $("[data-test-id='phone'] input").setValue("+79997770011");
        $("[data-test-id='agreement']").click();
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(),\"Заказ на выбранную дату невозможен\")]");
    }

    @Test
    void phoneTest() {
        $("[data-test-id='city'] input").setValue("Челябинск");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Тиньков Олег");
        $("[data-test-id='phone'] input").setValue("89997770011");
        $("[data-test-id='agreement']").click();
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(),'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.')]");
    }

    @Test
    void emptyFormTest() {
        $x("//span[@class=\"button__text\"]").click();
        $x("//*[contains(text(),'Поле обязательно для заполнения')]");
    }
}
