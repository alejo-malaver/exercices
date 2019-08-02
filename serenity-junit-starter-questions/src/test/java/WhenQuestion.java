import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import net.serenitybdd.junit.runners.SerenityRunner;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.*;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.questions.targets.TheTarget;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Managed;

import org.hamcrest.Matchers;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
//import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(SerenityRunner.class)
public class WhenQuestion {

	Actor actor;

	private static String CONTINUAR_A_PAGINA = ".footer-continue-link";
	private static Target TITULO_SECCION_DE_BUSQUEDA = Target.the("Título de la sección de búsqueda")
			.located(By.cssSelector(".cont-hd-alt.widget-query-heading"));
	private static Target CAJA_DE_BUSQUEDA_DESTINO = Target.the("Caja de texto para ingresar el destino a buscar")
			.located(By.id("qf-0q-destination"));
	private static Target SUGERENCIAS_DESTINO = Target.the("Caja de texto para ingresar el destino a buscar")
			.located(By.xpath("/html[1]/body[1]/div[4]/table[1]"));
	private static Target COMBO_HABITACIONES = Target.the("Lista de selección de habitaciones por cantidad de personas")
			.located(By.id("qf-0q-compact-occupancy"));
	private static Target BOTON_BUSCAR = Target.the("Botón 'Buscar' de la sección de búsquedas")
			.located(By.cssSelector(".cta.cta-strong"));
	private static Target CHECKBOX_UNA_ESTRELLA = Target.the("Checkbox para filtrar por hoteles de 1 estrella")
			.located(By.cssSelector("#f-star-rating-1"));
	private static Target CHECKBOX_DOS_ESTRELLAS = Target.the("Checkbox para filtrar por hoteles de 2 estrellas")
			.located(By.cssSelector("#f-star-rating-2"));
	private static Target CHECKBOX_TRES_ESTRELLAS = Target.the("Checkbox para filtrar por hoteles de 3 estrellas")
			.located(By.cssSelector("#f-star-rating-3"));
	private static Target CHECKBOX_CUATRO_ESTRELLAS = Target.the("Checkbox para filtrar por hoteles de 4 estrellas")
			.located(By.id("f-star-rating-4"));
	private static Target CHECKBOX_CINCO_ESTRELLAS = Target.the("Checkbox para filtrar por hoteles de 5 estrellas")
			.located(By.cssSelector("#f-star-rating-5"));
	private static Target TITULO_PRIMER_RESULTADO = Target.the("Título del primer resultado encontrado").located(By
			.xpath("/html[1]/body[1]/div[2]/main[1]/div[2]/div[2]/div[1]/section[2]/div[1]/ol[1]/li[1]/article[1]/section[1]/div[1]/h3"));

	private boolean existsElement(String css) {
		try {
			getDriver().findElement(By.cssSelector(css));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Managed
	WebDriver browser;

	@Before
	public void setStage() {
		actor = Actor.named("Maria");
		actor.can(BrowseTheWeb.with(browser));
	}

	@Test
	public void testQuestionsToElements() {
		actor.attemptsTo(Open.url("https://ar.hoteles.com/"));

		if (existsElement(CONTINUAR_A_PAGINA)) {
			actor.attemptsTo(Click.on(CONTINUAR_A_PAGINA));
		}

		actor.attemptsTo(Enter.theValue("Tun").into(CAJA_DE_BUSQUEDA_DESTINO),
				WaitUntil.the(SUGERENCIAS_DESTINO, isVisible()).forNoMoreThan(300).milliseconds(),
				Hit.the(Keys.ARROW_DOWN).into(CAJA_DE_BUSQUEDA_DESTINO),
				Hit.the(Keys.ENTER).into(CAJA_DE_BUSQUEDA_DESTINO),
				SelectFromOptions.byIndex(0).from(COMBO_HABITACIONES));

		// Una forma más sencilla de hacerlo pero no se maneja el tiempo de espera
		/*
		 * actor.attemptsTo(
		 * Enter.theValue("Tun").into(CAJA_DE_BUSQUEDA_DESTINO).thenHit(Keys.ARROW_DOWN)
		 * .thenHit(Keys.ENTER), SelectFromOptions.byIndex(0).from(COMBO_HABITACIONES));
		 */

		actor.should(seeThat(TheTarget.textOf(TITULO_SECCION_DE_BUSQUEDA), is("¿A dónde vas?")),
				seeThat(TheTarget.valueOf(CAJA_DE_BUSQUEDA_DESTINO), is("Tunja, Colombia")),
				seeThat(TheTarget.selectedValueOf(COMBO_HABITACIONES), is("1 habitación, 1 adulto")));

		actor.attemptsTo(Click.on(BOTON_BUSCAR), Scroll.to(CHECKBOX_CINCO_ESTRELLAS),
				Click.on(CHECKBOX_TRES_ESTRELLAS));
		// actor.attemptsTo(Scroll.to(CHECKBOX_TRES_ESTRELLAS));

		// En la primer línea se realiza una espera explícita, por default serenity da
		// una espera ímplicita de 5 seg cuando se recarga una página completa
		actor.should(
				eventually(seeThat(WebElementQuestion.the(CHECKBOX_CINCO_ESTRELLAS),
						Matchers.not(WebElementStateMatchers.isEnabled()))).waitingForNoLongerThan(1).seconds(),
				// seeThat(the(CHECKBOX_CUATRO_ESTRELLAS), isNotEnabled()),
				seeThat(the(CHECKBOX_TRES_ESTRELLAS), isSelected()),
				seeThat(the(CHECKBOX_DOS_ESTRELLAS), isNotSelected()),
				seeThat(the(CHECKBOX_UNA_ESTRELLA), not(isSelected())),
				seeThat(the(TITULO_PRIMER_RESULTADO), isVisible()),
				seeThat(the(TITULO_PRIMER_RESULTADO), containsText("Hotel Parque Santander Tunja")));
	}

}
