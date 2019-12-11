POST to the direction below and body will be 'raw' without format.

### localhost:8080/insert

{
	"tableName": "person",
	"attrs": {
		"name": "arnau",
		"surname": "mtez",
		"age":"24"
	}
}

and add many values to each table / column, then do...

### localhost:8080/select


<b>COUNT</b>
The count function can be used to count the rows returned by a query.

Assuming SELECT COUNT (*) FROM age;

{
	"operation": "count",
	"column": "age",
	"tableName": "person"
}

_________


<b>MAX AND MIN</b>
The max and min functions can be used to compute the maximum and the minimum value returned by a query for a given column.

SELECT MIN (age), MAX (age) FROM person

{
	"operation": "max",
	"column": "age",
	"tableName": "person"
}

{
	"operation": "min",
	"column": "age",
	"tableName": "person"
}

__________

<b>SUM</b>
The sum function can be used to sum up all the values returned by a query for a given column.

SELECT SUM (age) FROM person

{
	"operation": "sum",
	"column": "age",
	"tableName": "person"
}

______


<b>AVG </b>
The avg function can be used to compute the average of all the values returned by a query for a given column.

{
	"operation": "avg",
	"column": "age",
	"tableName": "person"
}