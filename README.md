# FINANCES TRACKER

A desktop app to keep track of personal income and expenses.

- - - - - -

## Description

The interface displays a list of [Transactions] in a table so that the user can
easily see how they've been managing their finances.

Every Transaction has 3 attributes:
+ the *amount* of money spent/gained
+ the *date* on which it happened
+ a short *description* of where that money came from or went to.

- - - - - -

## How it looks

On the left there's a section in which the user can create and add a new
Transaction to the list.<br>
The *title* is not required but highly recommended in order to remember what
the various transactions are for.<br>
Under the main table there's a small text section which shows the definitive
counters and a *Line Chart* that helps in better visualizing the changes over
time.

![Default interface]

- - - - - -

## Handling Data

The application lets the user save the current state to a binary file with the
"**Save**" button.<br>
The following time the application is started, it will automatically load the
data from that same file.

Moreover, there is an option to export the data to either a **.csv** or **.txt**
file that can at any time be imported back.<br>
If they choose, the user can even save the current state of the graph as a
**.png** image.<br>
All this can be done with the "**Export as...**" and "**Import from...**"
buttons.

![Export demo]

- - - - - -

## License

Distributed under the [MIT License].


<!-- Links -->

[Transactions]:
src/main/java/resources/Transaction.java

[Default interface]:
https://github.com/marcoplaitano/images/blob/main/finances_demo.png
"App screenshot"

[Export demo]:
https://github.com/marcoplaitano/images/blob/main/finances_demo_export.png
"Export data demo"

[MIT License]:
LICENSE
