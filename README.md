# Leturi Mapping

This is the workflow that I created at my summer internship in 2015. Manufacturers provide product lists which are to be categorized by a team, which is a fairly tedious task given that there might be more than 100,000 line items in a given list. I proposed to create a program to help with this.

It contains two parts - Java to create a template map and VBA to actually assign the new codes.

The Java program takes in a product list, analyzes patterns and mappings, then generates a template map for use by the VBA script to automatically categorize new products.

The VBA macro/script takes in the appropriate input by the user, finds the template map (what the Java script outputted), and maps the given list items.

**For confidentiality, no data, actual or otherwise, is shown - only backend code. Testing the program will require an input product list (to create a template map) and another file (to actually map).**

Some may find the logic in determining string patterns applicable to their projects. Contact for more information, including logic flow charts.

Contact: [Srikanth Chelluri](mailto:chelluri.srikanth@gmail.com) or Sean Letourneau.
