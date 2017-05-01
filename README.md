# CS 102 Final Project CFD (Computational Fluid Dynamics)
[Download Project (zip)](../../archive/master.zip)

<p align="center">
    <a href="https://drive.google.com/open?id=0By-CMfnYF6bZUVFIZG5GWFF3YVU"><img src="docs/media/cfd_thumb.png" alt="vimeo"/></a>
</p>

### Making your own simulation environment

To make your own simulation environment, modify
[Template.java](src/Template.java). Don't forget to include in your workspace:
* [StdDraw.java](src/StdDraw.java)
* [RetinaIcon.java](src/RetinaIcon.java)
* [Simulation.java](src/Simulation.java)

Take a look at the examples for some inspiration:
* [Example 1](#example-1)
* [Example 2](#example-2)
* [Example 3](#example-3)
* [Example 4](#example-4)
* [Example 5](#example-5)
* [CFD](#cfd)
* [CFD HD](#cfd-hd)

### Example 1
<p align="center">
  <img src="docs/media/example1.gif" width="400" height="160">
</p>

This is a simple simulation that has only one variable that determines the brightness of a pixel:

```java
double B[][] = new double[xdim][ydim];
```

Each value in the 2d array is set to mod 10 of the sum of x and y. This creates the diagonal pattern with numbers 0.0 to 9.0.

```java
B[x][y] = (x + y) % 10.0;
```
At each time step we shift the numbers over by one 0->1, 1->2, ... , 9->0. This is done by incramenting the value by one and apllying mod 10:

```java
B[x][y] = (B[x][y] + 1.0) % 10.0;
```

When we draw the pixels we take the `B[x][y]` value and scale it to a float value between 0.0f and 0.9f.
Then our new float value is used to determine the brightness value of our pixel.

```java
float b = (float) B[x][y] / 10.0f;
Color color = Color.getHSBColor(1.0f,0.0f,b);
```

Source code: [Example1.java](src/Example1.java)

### Example 2
<p align="center">
  <img src="docs/media/example2.gif" width="400" height="160">
</p>

Source code: [Example2.java](src/Example2.java)

### Example 3
<p align="center">
  <img src="docs/media/example3.gif" width="400" height="160">
</p>

Source code: [Example3.java](src/Example3.java)

### Example 4
<p align="center">
  <img src="docs/media/example4.gif" width="400" height="160">
</p>

Source code: [Example4.java](src/Example4.java)

### Example 5
<p align="center">
  <img src="docs/media/example5.gif" width="400" height="160">
</p>

Source code: [Example5.java](src/Example5.java)

### CFD
<p align="center">
  <img src="docs/media/cfd.gif" width="400" height="160">
</p>

Source code: [CFD.java](src/CFD.java)

### CFD HD
<p align="center">
  <img src="docs/media/cfd_hd_clip.gif" width="400" height="160">
</p>

Source code: [CFD_HD.java](src/CFD_HD.java)

### Final Notes
If you would like to download the full project including the screenshots and videos click this link: [Download Full Project (zip)](../../archive/full.zip). Just note that it is a large file.
