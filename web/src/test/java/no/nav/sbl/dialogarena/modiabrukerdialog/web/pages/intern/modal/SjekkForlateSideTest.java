package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

//@DirtiesContext(classMode = AFTER_CLASS)
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {KjerneinfoPepMockContext.class})
//public class SjekkForlateSideTest extends WicketPageTest {
//
//    private SjekkForlateSideAnswer answer;
//    private SjekkForlateSide sjekkForlateSide;
//
//    @Override
//    protected void additionalSetup() {
//        answer = new SjekkForlateSideAnswer();
//        sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);
//    }
//
//    @Test
//    public void skalOppretteSjekkForlateSide() {
//        wicket.goToPageWith(sjekkForlateSide)
//                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
//                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
//    }
//
//    @Test
//    public void skalReturnereCancelAswer() {
//        wicket.goToPageWith(sjekkForlateSide)
//                .click().link(withId("closeCancel"));
//        assertTrue(answer.is(CANCEL));
//        assertFalse(answer.is(DISCARD));
//    }
//
//    @Test
//    public void skalReturnereDiscardAswer() {
//        wicket.goToPageWith(sjekkForlateSide)
//                .click().link(withId("closeDiscard"));
//        assertTrue(answer.is(DISCARD));
//        assertFalse(answer.is(CANCEL));
//    }
//
//}
