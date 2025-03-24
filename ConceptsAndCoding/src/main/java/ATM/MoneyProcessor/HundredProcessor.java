package ATM.MoneyProcessor;

import ATM.ATMMachine;

public class HundredProcessor extends NotesProcessor{
  NotesProcessor nextNoteProcessor;

  public HundredProcessor(NotesProcessor nextNoteProcessor) {
    super(nextNoteProcessor);
  }


  public void setNextNoteProcessor(int amt, ATMMachine atm) {
    int notes = atm.getHundredNotes();
    if (amt < 100) {
      super.setNextNoteProcessor(amt,atm);
    }
    else if (notes <=  amt/100) {
      amt = amt - notes * 100;
      System.out.println("Giving " + notes + " 100 notes");
      atm.setHundredNotes(0);
      super.setNextNoteProcessor(amt, atm);
    } else if (notes >  amt/100) {
      notes = notes - amt/100;
      atm.setHundredNotes(notes);
      int notesGiven =  amt/100;
      System.out.println("Giving " + notesGiven + " 100 notes");
      amt = amt - 100*notesGiven;
      super.setNextNoteProcessor(amt, atm);
    }
  }
}
