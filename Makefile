# Makefile for yoob and its bundled languages.
# The contents of this file have been placed in the public domain.

JAVAC?=javac
JAVA?=java
JFLAGS?=-Xlint:deprecation -Xlint:unchecked
PATHSEP?=:

YOOBDIR?=.
CLASSPATH?=bin$(PATHSEP)$(YOOBDIR)/bin

CDIR=bin/tc/catseye/yoob

CLASSES=$(CDIR)/Element.class \
        $(CDIR)/Tape.class \
        $(CDIR)/Playfield.class \
        $(CDIR)/World.class \
        $(CDIR)/State.class \
        $(CDIR)/Error.class \
        $(CDIR)/View.class \
        $(CDIR)/Criterion.class \
        $(CDIR)/Matcher.class \
        \
        $(CDIR)/TextBasedLanguage.class \
        $(CDIR)/ExampleProgram.class \
        \
        $(CDIR)/Position.class \
        $(CDIR)/IntegerElement.class \
        $(CDIR)/CharacterElement.class \
        $(CDIR)/BitElement.class \
        $(CDIR)/ByteElement.class \
        $(CDIR)/Int32Element.class \
        \
        $(CDIR)/EqualityMatcher.class \
        \
        $(CDIR)/BasicTape.class \
        $(CDIR)/BasicHead.class \
        $(CDIR)/BasicStack.class \
        $(CDIR)/BasicQueue.class \
        $(CDIR)/BasicPlayfield.class \
        $(CDIR)/BasicCursor.class \
        \
        $(CDIR)/AbstractView.class \
        $(CDIR)/BasicPlayfieldView.class \
        $(CDIR)/BasicTapeView.class \
        \
        $(CDIR)/CellularAutomatonPlayfield.class \
        $(CDIR)/CommonPlayfield.class \
        $(CDIR)/OverlayPlayfield.class \
        $(CDIR)/WrapCursor.class \
        \
        $(CDIR)/AbstractDepiction.class \
        $(CDIR)/TapeDepiction.class \
        $(CDIR)/PlayfieldDepiction.class \
        $(CDIR)/TextAreasWorld.class \
        $(CDIR)/ContentPane.class \
        \
        $(CDIR)/EsolangLoader.class \
        $(CDIR)/Applet.class \
        $(CDIR)/GUI.class

LANGCLASSES=$(CDIR)/backflip/BackFlipState.class \
        $(CDIR)/befunge93/Befunge93State.class \
        $(CDIR)/black/BlackState.class \
        $(CDIR)/bf/BrainfuckState.class \
        $(CDIR)/circute/CircuteState.class \
        $(CDIR)/etcha/EtchaState.class \
        $(CDIR)/lnusp/LNUSPState.class \
        $(CDIR)/gemooy/GemooyState.class \
        $(CDIR)/onela/OneLaState.class \
        $(CDIR)/onelaoi/OneLAOIState.class \
        $(CDIR)/path/PATHState.class \
        $(CDIR)/qdeql/QdeqlState.class \
        $(CDIR)/sceql/SceqlState.class \
        $(CDIR)/smetana/SMETANAState.class \
        $(CDIR)/snusp/SNUSPState.class \
        $(CDIR)/twoill/TwoIllState.class \
        $(CDIR)/twol/TwoLState.class \
        $(CDIR)/wunnel/WunnelState.class

all: yoob langs

yoob: $(CLASSES)

langs: yoob $(LANGCLASSES)

$(CDIR)/Element.class: src/Element.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Element.java

# $(CDIR)/Head.class: src/Head.java
$(CDIR)/Tape.class: src/Tape.java src/Head.java $(CDIR)/IntegerElement.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Head.java src/Tape.java

# $(CDIR)/Cursor.class: src/Cursor.java
$(CDIR)/Playfield.class: src/Playfield.java src/Cursor.java $(CDIR)/IntegerElement.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Cursor.java src/Playfield.java

$(CDIR)/Error.class: src/Error.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Error.java

$(CDIR)/World.class: src/World.java $(CDIR)/CharacterElement.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/World.java

# $(CDIR)/Language.class: src/Language.java
$(CDIR)/State.class: src/State.java $(CDIR)/Playfield.class $(CDIR)/Tape.class $(CDIR)/View.class $(CDIR)/Error.class $(CDIR)/World.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/State.java src/Language.java

$(CDIR)/View.class: src/View.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/View.java

$(CDIR)/Criterion.class: src/Criterion.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Criterion.java

$(CDIR)/Matcher.class: src/Matcher.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Matcher.java

$(CDIR)/TextBasedLanguage.class: src/TextBasedLanguage.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/TextBasedLanguage.java

$(CDIR)/ExampleProgram.class: src/ExampleProgram.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/ExampleProgram.java

$(CDIR)/Position.class: src/Position.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Position.java

$(CDIR)/IntegerElement.class: src/IntegerElement.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/IntegerElement.java

$(CDIR)/CharacterElement.class: src/CharacterElement.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/CharacterElement.java

$(CDIR)/BitElement.class: src/BitElement.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BitElement.java

$(CDIR)/ByteElement.class: src/ByteElement.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/ByteElement.java

$(CDIR)/Int32Element.class: src/Int32Element.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Int32Element.java

$(CDIR)/EqualityMatcher.class: src/EqualityMatcher.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/EqualityMatcher.java

$(CDIR)/AbstractView.class: src/AbstractView.java $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/AbstractView.java

$(CDIR)/BasicPlayfieldView.class: src/BasicPlayfieldView.java $(CDIR)/Playfield.class $(CDIR)/AbstractView.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicPlayfieldView.java

$(CDIR)/BasicTapeView.class: src/BasicTapeView.java $(CDIR)/AbstractView.class $(CDIR)/Tape.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicTapeView.java

$(CDIR)/BasicHead.class: src/BasicHead.java $(CDIR)/Tape.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicHead.java

$(CDIR)/BasicTape.class: src/BasicTape.java $(CDIR)/BasicHead.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicTape.java

$(CDIR)/BasicStack.class: src/BasicStack.java $(CDIR)/BasicTape.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicStack.java

$(CDIR)/BasicQueue.class: src/BasicQueue.java $(CDIR)/BasicTape.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicQueue.java

$(CDIR)/BasicCursor.class: src/BasicCursor.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicCursor.java

$(CDIR)/BasicPlayfield.class: src/BasicPlayfield.java $(CDIR)/Playfield.class $(CDIR)/BasicCursor.class $(CDIR)/Position.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/BasicPlayfield.java

$(CDIR)/CellularAutomatonPlayfield.class: src/CellularAutomatonPlayfield.java $(CDIR)/BasicPlayfield.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/CellularAutomatonPlayfield.java

$(CDIR)/CommonPlayfield.class: src/CommonPlayfield.java $(CDIR)/BasicPlayfield.class $(CDIR)/CharacterElement.class $(CDIR)/Position.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/CommonPlayfield.java

$(CDIR)/OverlayPlayfield.class: src/OverlayPlayfield.java $(CDIR)/Playfield.class $(CDIR)/Element.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/OverlayPlayfield.java

$(CDIR)/WrapCursor.class: src/WrapCursor.java $(CDIR)/BasicCursor.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/WrapCursor.java

$(CDIR)/AbstractDepiction.class: src/AbstractDepiction.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/AbstractDepiction.java

$(CDIR)/TapeDepiction.class: src/TapeDepiction.java $(CDIR)/Tape.class $(CDIR)/AbstractDepiction.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/TapeDepiction.java

$(CDIR)/PlayfieldDepiction.class: src/PlayfieldDepiction.java $(CDIR)/Playfield.class $(CDIR)/AbstractDepiction.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/PlayfieldDepiction.java

$(CDIR)/TextAreasWorld.class: src/TextAreasWorld.java $(CDIR)/World.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/TextAreasWorld.java

# $(CDIR)/RunThread.class: src/RunThread.java
$(CDIR)/ContentPane.class: src/ContentPane.java src/RunThread.java $(CDIR)/PlayfieldDepiction.class $(CDIR)/TextAreasWorld.class $(CDIR)/EsolangLoader.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/ContentPane.java src/RunThread.java

$(CDIR)/EsolangLoader.class: src/EsolangLoader.java
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/EsolangLoader.java

$(CDIR)/GUI.class: src/GUI.java $(CDIR)/ContentPane.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/GUI.java

$(CDIR)/Applet.class: src/Applet.java $(CDIR)/ContentPane.class
	$(JAVAC) $(JFLAGS) -cp bin -d bin src/Applet.java

ESOLANGS?=tc.catseye.yoob.backflip.BackFlipState/BackFlip \
          tc.catseye.yoob.befunge93.Befunge93State/Befunge-93 \
          tc.catseye.yoob.black.BlackState/Black \
          tc.catseye.yoob.bf.BrainfuckState/brainfuck \
          tc.catseye.yoob.circute.CircuteState/Circute \
          tc.catseye.yoob.etcha.EtchaState/Etcha \
          tc.catseye.yoob.gemooy.GemooyState/Gemooy \
          tc.catseye.yoob.lnusp.LNUSPState/LNUSP \
          tc.catseye.yoob.onela.OneLaState/1L_a \
          tc.catseye.yoob.onelaoi.OneLAOIState/1L_AOI \
          tc.catseye.yoob.path.PATHState/PATH \
          tc.catseye.yoob.qdeql.QdeqlState/Qdeql \
          tc.catseye.yoob.sceql.SceqlState/Sceql \
          tc.catseye.yoob.smetana.SMETANAState/SMETANA \
          tc.catseye.yoob.snusp.SNUSPState/SNUSP \
          tc.catseye.yoob.twoill.TwoIllState/2-ill \
          tc.catseye.yoob.twol.TwoLState/2L \
          tc.catseye.yoob.wunnel.WunnelState/Wunnel

$(CDIR)/backflip/BackFlipState.class: src/lang/BackFlipState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/BackFlipState.java

$(CDIR)/befunge93/Befunge93State.class: src/lang/Befunge93State.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/Befunge93State.java

$(CDIR)/black/BlackState.class: src/lang/BlackState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/BlackState.java

$(CDIR)/bf/BrainfuckState.class: src/lang/BrainfuckState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/BrainfuckState.java

$(CDIR)/circute/CircuteState.class: src/lang/CircuteState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/CircuteState.java

$(CDIR)/etcha/EtchaState.class: src/lang/EtchaState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/EtchaState.java

$(CDIR)/gemooy/GemooyState.class: src/lang/GemooyState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/GemooyState.java

$(CDIR)/lnusp/LNUSPState.class: src/lang/LNUSPState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/LNUSPState.java

$(CDIR)/onela/OneLaState.class: src/lang/OneLaState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/OneLaState.java

$(CDIR)/onelaoi/OneLAOIState.class: src/lang/OneLAOIState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/OneLAOIState.java

$(CDIR)/path/PATHState.class: src/lang/PATHState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/PATHState.java

$(CDIR)/qdeql/QdeqlState.class: src/lang/QdeqlState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/QdeqlState.java

$(CDIR)/sceql/SceqlState.class: src/lang/SceqlState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/SceqlState.java

$(CDIR)/smetana/SMETANAState.class: src/lang/SMETANAState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/SMETANAState.java

$(CDIR)/snusp/SNUSPState.class: src/lang/SNUSPState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/SNUSPState.java

$(CDIR)/twoill/TwoIllState.class: src/lang/TwoIllState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/TwoIllState.java

$(CDIR)/twol/TwoLState.class: src/lang/TwoLState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/TwoLState.java

$(CDIR)/wunnel/WunnelState.class: src/lang/WunnelState.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" -d bin src/lang/WunnelState.java

clean:
	rm -rf $(CDIR)/*.class
	rm -rf $(CDIR)/*/*.class

test: langs
	$(JAVA) -cp "$(CLASSPATH)" tc.catseye.yoob.GUI -c "$(ESOLANGS)"
