package ATM.MoneyProcessor;

import ATM.ATMMachine;

public class FiveHundredProcessor extends NotesProcessor {

  NotesProcessor nextNoteProcessor;

  public FiveHundredProcessor(NotesProcessor nextNoteProcessor) {
    super(nextNoteProcessor);
  }

  public void setNextNoteProcessor(int amt, ATMMachine atm) {
    int notes = atm.getFiveHundredNotes();
    if (amt < 500) {
      super.setNextNoteProcessor(amt, atm);
    }
    else if (notes <=  amt/500) {
      amt = amt - notes * 500;
      System.out.println("Giving " + notes + " 500 notes");
      atm.setFiveHundredNotes(0);
      super.setNextNoteProcessor(amt, atm);
    } else if (notes >  amt/500) {
      notes = notes - amt/500;
      atm.setFiveHundredNotes(notes);
      int notesGiven = amt/500;
      System.out.println("Giving " + notesGiven + " 500 notes");
      amt = amt - 500*notesGiven;
      super.setNextNoteProcessor(amt, atm);
    }
  }
}
