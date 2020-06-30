package com.google.sps.Objects.comparator;

import java.util.Comparator;

import com.google.sps.Objects.Option;

/**
 * Comparator that compares the number of votes between poll options. Used to
 * sort options in descending order based on number of votes per option.
 *
 * @author lucyqu
 *
 */
public class OptionsComparator implements Comparator<Option> {

  @Override
  public int compare(Option o1, Option o2) {
    if (o1.getVotes() != null && o2.getVotes() != null) {
      return Integer.compare(o2.getVotes().size(), o1.getVotes().size());
    } else if (o1.getVotes() == null && o2.getVotes() == null) {
      return 0;
    } else if (o2.getVotes() == null) {
      return Integer.compare(0, o1.getVotes().size());
    } else if (o1.getVotes() == null) {
      return Integer.compare(o2.getVotes().size(), 0);
    }
    return 0;
  }

}
