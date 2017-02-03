# Thunderbirds-are-GO

![Go Nedap](https://lh3.googleusercontent.com/auTNfJrwgwucKHhXd1a0ByaE1LIvM978jw869umNKTEVAhL0-W7H8ikX-7O-pJdDMQ7zJw=w1264 "Go Nedap")

######Instructions
1. Get the [latest release](https://github.com/erikhuizinga/Thunderbirds-are-GO/releases/latest)
1337. Get the [`gogui` JAR](https://github.com/BlueWizardNedap/gogui/blob/master/bin/gui-1.1.jar) and make sure it's available on the path.
1337. Compile, e.g. with IntelliJ IDEA and JDK 1.8
1337. Fire up a terminal or run from IDEA:
    - `ui.tui.TUI.main()`
    - `net.Server.main()`, then a number of `net.Client.main()`
      - Pay attention to the console: a number of input arguments is required for both server and client, which is printed to `System.out` if incorrect syntax is used
      - The default `Server` host is `localhost`
1337. Let's GO!

######Development status
- The TUI is functional, but not entirely implemented. You'll find only the two player menu works.
- The GUI can be enabled through the TUI, as the GUI only acts as a view of the Go board and the TUI handles the rest of the UI.
- The client and server don't achieve much (in fact, nothing), but they can be run while inspecting/debugging the code to see that they run fine.