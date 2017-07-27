#!/usr/bin/make -f

## APP TESTING ##
STORAGE=./storage
MAIN=ninja.faust.smartscale.SmartScale

## JAVA STUFF ##
JC=javac
JAVA=java
JAR=jar
DST=build/
SRCDIRS=ninja/ org/
SRC=$(foreach dir,$(SRCDIRS),$(shell find $(dir) -iname *.java))
TARGET_JAR=SmartScale.jar

## DALVIK STUFF ##
ART_BASE=./art
ART=$(ART_BASE)/bin/art
DX=$(ART_BASE)/bin/dx
D2O=$(ART_BASE)/bin/dex2oat
TARGET_DEX=SmartScale.dex

.PHONY: smartscale clean run-java art-usage

## DALVIK STUFF ##
$(TARGET_DEX): $(TARGET_JAR)
	$(DX) --dex --output=$@ $< 

art-usage:
	$(ART) -cp $(ART_BASE)/Hello.dex Hello

run-dex: $(TARGET_DEX)
	$(ART) -cp $< $(MAIN) $(STORAGE)

## JAVA STUFF ##
$(TARGET_JAR): smartscale
	$(JAR) cf $@ -C $(DST) .

smartscale: $(SRC)
	$(shell mkdir -p $(DST))
	$(JC) -source 1.7 -target 1.7 $^ -d $(DST)

run-java: $(TARGET_JAR)
	$(JAVA) -cp $< $(MAIN) $(STORAGE)
	
	
clean:
	$(RM) -rf $(DST) *.jar *.dex
