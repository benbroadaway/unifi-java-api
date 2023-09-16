package org.benbroadaway.unifi.cli.completion;

import java.util.ArrayList;
import java.util.List;

public class BooleanCandidates extends ArrayList<String> {
    BooleanCandidates() { super(List.of("true", "false")); }
}
