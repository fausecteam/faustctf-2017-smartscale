IoT Device: Waage

CTF Daten:

    * Flag

Daten:

    * Gewicht
    * Groesse
    * Fettanteil
    * BMI
    * Kommentar (Flag here?)
    * ...

Persistenter Speicher:

    * Dateisystem
        * FlagID --> Name der Datei
        * JSON-Obj aus Daten

Crypto:

    * esoterischer Hash (broken md5) ueber JSON-Obj ohne Hash, wird anschliessend hinzugefuegt
    * in Java und dann Bytecode-Stripping

Vulns:

    * FlagID aufsteigend generiert, Fix durch UUID bzw. Random Value fuer FlagID
    * Java Introspection: calculateBMI() via JSON-Obj --> public getAllFlagIDs()
    * Binary-Exploitation Schwachstelle: String foo(String bar)

Interaktion:

    * Store:
        Waage: Gib mir neue Daten
        GS: Hier meine Daten { "weight" : "70", "size" : "1,80", ..., "comment" : "<flag>" }
        Waage: OK! Hier hast du die FlagID.

    * Retrieve:
        Waage: Welche Daten willst du?
        GS:    FlagID!
        Waage: { "weight" : "70", "size" : "1,80", ..., "comment" : "<flag>" }

Beispiele:

	Input:  {"action":"store",data:{"weight":70,"size":1.8,"fat_quotient":0.2,"comment":"FLAG_SHOULD_BE_HERE"}}
	Output: {"status":"ok","flag_id":"5f1eabc89d209a2"}
	
	Input:  {"action":"store",data:{"weight":70,"size":1.8,"fat_quotient":0.2,"comment":"FLAG_SHOULD_BE_HERE","tasks":["calculateBMI"]}}
	Output: {"status":"ok","flag_id":"5f1eabc89d209ac"}
	
	Input:  {"action":"store",data:{"weight":70,"size":1.8,"fat_quotient":0.2,"comment":"FLAG_WILL_NEVER_BE_SAVED","tasks":["magic"]}}
	Output: {"status":"ok","flag_ids":["5f1eabc89d209a2","5f1eabc89d209ac"]}
	
	Input:  {"action":"retrieve",flag_id:"5f1eabc89d209ac"}
	Output: {"status":"ok","data":{"weight":70,"size":1.8,"fat_quotient":0.2,"bmi":21.604938271604937,"comment":"FLAG_SHOULD_BE_HERE","hash":"0d505f1c352d5fb6692e57984b838978"}}
	