package ATM.MoneyProcessor;

import ATM.ATMMachine;

public class TwoThousandProcessor extends NotesProcessor{
  NotesProcessor nextNoteProcessor;


  public TwoThousandProcessor( NotesProcessor nextNoteProcessor) {
    super(nextNoteProcessor);

  }


  public void setNextNoteProcessor(int amt, ATMMachine atm) {
    int notes = atm.getTwoThousandNotes();
    if (amt < 2000) {
      super.setNextNoteProcessor(amt, atm);
    }
    else if (notes <=  amt/2000) {
      amt = amt - notes * 2000;
      System.out.println("Giving " + notes + " 2k notes");
      atm.setTwoThousandNotes(0);
      super.setNextNoteProcessor(amt, atm);
    } else if (notes >  amt/2000) {
      notes = notes - amt/2000;
      atm.setTwoThousandNotes(notes);
      int notesGiven = amt/2000;
      System.out.println("Giving " + notesGiven + " 2k notes");
      amt = amt - 2000*notesGiven;
      super.setNextNoteProcessor(amt, atm);
    }
  }

}
