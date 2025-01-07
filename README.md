"# FidoFoodFinder" 

**Fido's Food Quest 🐕**

A Clojure-based pathfinding game where you help Fido navigate through mazes to find food. The program uses breadth-first search to find the optimal path and visualizes the search process.

**Features**

Interactive menu-driven interface
Reads custom maze maps from text files
Visualizes the pathfinding process with:

_+ marking the optimal path
! showing explored areas
@ representing food
# for walls
- for open spaces_


Supports any rectangular maze format
Real-time path visualization
Built-in map file browser

**Usage**

clojureCopy; Run the program
clj -M fido.clj

; Map format example:
-#--#
--#-@
#----

**Technical Details**

Written in Clojure
Uses breadth-first search algorithm
Functional programming approach
Clean, modular code structure
No external dependencies

**Map Legend**

@ : Food (goal)
# : Wall
- : Open path
+ : Solution path
! : Explored areas

