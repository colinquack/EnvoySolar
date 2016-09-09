var dashboard, dateSlider, comboChart;
var redColor = '#DC3912';
var blueColor = '#3366CC';
var waitingToDraw = true;
var minuteNudge = 20;
var range = 'day';
var mode = 'day';

$(document).ready(function() {
    $('h1').bind("click", h1Click);
    
    $(".buttonbar").append('<select class="inner" id="rangeSelect" disabled/>');
    $(".buttonbar").append('<input id="datepicker" type="date" disabled>');
    $(".buttonbar").append('<button class="inner extraButton" id="today" type="button" disabled>Today</button>');
    $(".buttonbar").append('<button class="inner extraButton" id="yesterday" type="button" disabled>Yesterday</button>');
    $(".buttonbar").append('<button class="inner" id="refresh" type="button" disabled>Refresh</button>');

    $("#rangeSelect").append("<option value='day'>Day</option>");
    $("#rangeSelect").append("<option value='week'>Week</option>");
    $("#rangeSelect").append("<option value='month'>Month</option>");
    $("#rangeSelect").append("<option value='custom'>Custom</option>");
                
    $("#rangeSelect").change(function() {rangeChange(this.value);});
    $("#datepicker").change(function() {showDay(this.value);});
    $("#today").click(function() {periodChange('today');});
    $("#yesterday").click(function() {periodChange('yesterday');});
    $("#refresh").click(drawDashboard);
    
    $('#dashboard_div').append('<div id="filter_div"/>');
    $('#dashboard_div').append('<div id="chart_div"/>');
    
    $.ajaxSetup({cache:false});

    google.charts.load('current', {'packages':['corechart', 'table', 'gauge', 'controls'], 'language': 'en_GB'});
    google.charts.setOnLoadCallback(createDashboard);
});

function h1Click(event) {
    window.open("mobile.jsp", "_self");
}

function setAllDisabled(tagName, value) {
    var elem = document.getElementsByTagName(tagName);
    for (var i = 0; i < elem.length; i++) {
        elem[i].disabled = value;
    }
}

function setDisabled(value) {
    waitingToDraw = value;
    document.getElementById('datepicker').disabled = value;
    setAllDisabled("button", value);
    setAllDisabled("select", value);
}

function dateInputFormat(dateString) {
    var date = new Date(dateString);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var monthString = month.toString();
    if (month < 10) {
        monthString = "0" + monthString;
    }
    var dayString = day.toString();
    if (day < 10) {
        dayString = "0" + dayString;
    }

    return year.toString() + "-" + monthString + "-" + dayString;
}

function createDashboard() {
    dashboard = new google.visualization.Dashboard(document.getElementById('dashboard_div'));
    var today = new Date();
    var before = new Date();
    before.setHours(0);
    before.setMinutes(0 + minuteNudge);
    before.setSeconds(0);

    dateSlider = new google.visualization.ControlWrapper({
        controlType: 'ChartRangeFilter',
        containerId: 'filter_div',
        options: {
            filterColumnLabel: 'DateTime',
            highThumbAtMaximum: true,
            ui: {
                chartType: 'LineChart',
                chartView: {
                    'columns': [0, 3]
                },
                chartOptions: {
                    hAxis: {
                        //textPosition: 'out',
                        format: 'd MMMM'
                    },
                    series: {
                        0: {
                            color: redColor
                        }
                    }
                },
                cssClass: 'controlWrapper'
            }
        },
        state: {
            range: {
                start: before,
                end: today
            }
        }
    });

    google.visualization.events.addListener(dateSlider, 'statechange', dateSliderChange);

    var options = {
        title : 'Solar Production',
        interpolateNulls: true,
        animation: {
            duration: 0,
            easing: 'linear',
            startup: false
        },
        series: {
            0: {
              targetAxisIndex: 0,
              color: blueColor
            },
            1: {
              targetAxisIndex: 1,
              type: 'line',
              lineWidth: 5,
              color: redColor
            },
            2: {
              type: 'none',
              color: 'white',
              visibleInLegend: false
            }
        },
        seriesType: 'bars',
        legend: {
            position: 'top'
        },
        vAxes: [
            {
              title: 'Currently (W)',
              titleTextStyle: {
                  color: blueColor
                  //fontSize: 16,
                  //bold: true
              },
              textStyle: {
                  color: blueColor
              }
            },
            {
              title: 'Total (kWh)',
              titleTextStyle: {
                  color: redColor
                  //fontSize: 16,
                  //bold: true
              },
              textStyle: {
                  color: redColor
              }
            }
        ],
        hAxis: {
            title: 'Date and Time',
            titleTextStyle: {
                //color: 'black',
                //fontName: 'Arial',
                //fontSize: '24',
                //bold: true,
                //italic: true
            },
            allowContainerBoundaryTextCufoff: true,
            //viewWindowMode: 'pretty',
            format: 'dd MMM HH:mm'
        },
        chartArea: {
            top: 70
            //height: '40%'
        },
        //backgroundColor: '#faf6f1',
        tooltip: {
            //isHtml: true
            showColorCode: true,
            textStyle: {
                //color: 'green',
                //fontName: 'Arial',
                //fontSize: '16',
                //bold: true,
                //italic: true
            },
            ignoreBounds: true
        }
      };

    comboChart  = new google.visualization.ChartWrapper({
        'chartType': 'ComboChart',
        'containerId': 'chart_div',
        'options': options
    });

    google.visualization.events.addListener(comboChart, 'ready', chartReady);

    dashboard.bind(dateSlider, comboChart);
    setXAxisFormat('halfday');
    //periodChange('today');
    drawDashboard();
}

function redrawDashboard() {
    if (!waitingToDraw) {
        dateSlider.draw();
    }
}

function drawDashboard() {
    $.getJSON("getSolarData", {mode: mode}, function(response) {
        var data = new google.visualization.DataTable(response);

        var timeCol = 0;
        var startDate = dateInputFormat(data.getValue(0, timeCol));
        var endDate = dateInputFormat(data.getValue(data.getNumberOfRows() - 1, timeCol));

        var datepicker = document.getElementById('datepicker');
        datepicker.setAttribute("min", startDate);
        datepicker.setAttribute("max", endDate);
        datepicker.value = endDate;
        setDisabled(false);
        dashboard.draw(data);
    });
}

function chartReady() {
    google.visualization.events.addListener(comboChart.getChart(), 'animationfinish', animationfinish);
}

function animationfinish() {
    comboChart.setOption('animation.duration', 0);
}

function getActualRangeGap() {
    var state = dateSlider.getState();
    var start = new Date(state.range.start);
    var end = new Date(state.range.end);
    return Math.round((end.valueOf() - start.valueOf()) / 1000 / 60 / 60 / 24);
}

function getRangeGap() {
    var value = document.getElementById('rangeSelect').value;
    switch(value) {
        case "day":
            return 1;
            break;
        case "week":
            return 7;
            break;
        case "month":
            return 30;
            break;
        case "year":
            return 365;
            break;
        default:
            return getActualRangeGap();
    }
}

function dateSliderChange() {
    var gap = getActualRangeGap();

    switch(gap) {
        case 1:
            setRangeDropDown('day');
            break;
        case 7:
            setRangeDropDown('week');
            break;
        case 30:
            setRangeDropDown('month');
            break;
        default:
            setRangeDropDown('custom');
            break;
    }

    if (gap <= 1) {
        setXAxisFormat("halfday");
    } else if (gap < 4) {
        setXAxisFormat("day");
    } else if (gap < 10) {
        setXAxisFormat("week");
    } else if (gap < 45) {
        setXAxisFormat("month");
    } else {
        setXAxisFormat("year");
    }

    var state = dateSlider.getState();
    document.getElementById('datepicker').value = dateInputFormat(new Date(state.range.end));
}

function setRangeDropDown(value) {
    var element = document.getElementById('rangeSelect');
    element.value = value;
}

function setXAxisFormat(value) {
    var dateFormat = "dd MMMM";
    var title = "Date and Time";

    switch(value) {
        case "halfday":
            dateFormat = "HH:mm";
            title = "Time";
            break;
        case "day":
            dateFormat = "HH";
            title = "Time";
            break;
        case "week":
            dateFormat = "EEEE";
            title = "Day";
            break;
        case "month":
            dateFormat = "dd MMMM";
            title = "Date";
            break;
        case "year":
            dateFormat = "MMMM";
            title = "Month";
            break;
    }

    comboChart.setOption('hAxis.format', dateFormat);
    comboChart.setOption('hAxis.title', title);
}

function showDay(value) {
    var before = new Date(value);
    before.setDate(before.getDate() - (getRangeGap() - 1));
    before.setHours(0);
    before.setMinutes(0 + minuteNudge);
    before.setSeconds(0);

    var after = new Date(value);
    after.setHours(23);
    after.setMinutes(59 - minuteNudge);
    after.setSeconds(59);

    comboChart.setOption('animation.duration', 1000);

    var state = {
        range: {
            start: before,
            end: after
        }
    };
    dateSlider.setState(state);
    dateSliderChange();
    dateSlider.draw();
}

function periodChange(value) {
    var day = new Date();
    day.setHours(0);
    day.setMinutes(0 + minuteNudge);
    day.setSeconds(0);

    switch(value) {
        case "today":
            break;
        case "yesterday":
            day.setDate(day.getDate() - 1);
            break;
        default:
            return;
    }

    document.getElementById('datepicker').value = dateInputFormat(day);
    setRangeDropDown('day');
    showDay(day);
}

function dataChange(value) {
    drawDashboard();
}

function rangeChange(value) {
    var offset = 1;
    var change = false;
    switch(value) {
        case "day":
            offset = 1;
//            if (range !== 'day') {
//                mode = 'day';
//                change = true;
//            }
            break;
        case "week":
            offset = 7;
//            if (range === 'day') {
//                mode = 'max';
//                change = true;
//            }
            break;
        case "month":
            offset = 30;
//            if (range === 'day') {
//                mode = 'max';
//                change = true;
//            }
            break;
        case "year":
            offset = 365;
//            if (range === 'day') {
//                mode = 'max';
//                change = true;
//            }
            break;
        default:
            return;
    }
            
    range = value;

    var state = dateSlider.getState();
    var before = new Date(state.range.end);
    var after = new Date(state.range.end);
    after.setHours(23);
    after.setMinutes(59 - minuteNudge);
    after.setSeconds(59);
    before.setDate(after.getDate() - offset);
    state.range.start = before;
    state.range.end = after;

    dateSlider.setState(state);
    dateSliderChange();
    dateSlider.draw();
    if (change) {
        drawDashboard();
    }
}
