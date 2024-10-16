# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:In my implementation, I used the bottom-left point as the anchor, whereas you used the top-left point as the anchor.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:The hexagon corresponds to the room in Project 3, while the tessellation of hexagons corresponds to the layout of rooms and hallways.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:A method to generate a room.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:Differences:

Function: Rooms are typically enclosed spaces used to house players or objects, while hallways are passages that connect rooms, primarily providing movement paths.

Shape: Rooms are usually larger rectangles or other polygonal shapes, whereas hallways are typically narrow linear paths with a more confined shape.

Layout: Rooms can exist independently, but hallways must connect two or more rooms, serving as a bridge between them.

Similarities:

Structure: Both rooms and hallways are fundamental units in world generation. They are part of the world and contribute to the overall map structure.

Generation method: In the world generation process, both rooms and hallways can be generated through similar procedural methods, including randomly determining their size and position.







