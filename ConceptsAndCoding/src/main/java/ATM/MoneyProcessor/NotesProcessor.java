package ATM.MoneyProcessor;

import ATM.ATMMachine;

public class NotesProcessor {
  NotesProcessor nextNoteProcessor;

  public NotesProcessor(NotesProcessor nextNoteProcessor) {
    this.nextNoteProcessor = nextNoteProcessor;

  }
  public void setNextNoteProcessor(int amt,  ATMMachine atm) {
    if (nextNoteProcessor != null) {
      nextNoteProcessor.setNextNoteProcessor(amt, atm);
    }
  }
}
