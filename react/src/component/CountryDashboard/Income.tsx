import * as React from 'react';
import { PieChart, pieArcLabelClasses, type PieSeries } from '@mui/x-charts/PieChart';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { useDrawingArea } from '@mui/x-charts/hooks';
import { styled } from '@mui/material/styles';
import type { Theme } from '@mui/material/styles';

interface TitanicDatum {
    Class: '1st' | '2nd' | '3rd' | 'Crew';
    Survived: 'Yes' | 'No';
    Count: number;
}

interface ChartDatum {
    id: string;
    label: string;
    value: number;
    percentage: number;
    color: string;
}

type ClassType = '1st' | '2nd' | '3rd' | 'Crew';

// https://en.wikipedia.org/wiki/Passengers_of_the_Titanic#/media/File:Titanic_casualties.svg
const titanicData: TitanicDatum[] = [
    { Class: '1st', Survived: 'No', Count: 123 },
    { Class: '1st', Survived: 'Yes', Count: 202 },
    { Class: '2nd', Survived: 'No', Count: 167 },
    { Class: '2nd', Survived: 'Yes', Count: 118 },
    { Class: '3rd', Survived: 'No', Count: 528 },
    { Class: '3rd', Survived: 'Yes', Count: 178 },
    { Class: 'Crew', Survived: 'No', Count: 696 },
    { Class: 'Crew', Survived: 'Yes', Count: 212 },
];

const classes: ClassType[] = ['1st', '2nd', '3rd', 'Crew'];

const totalCount = titanicData.reduce(
    (acc: number, item: TitanicDatum) => acc + item.Count,
    0,
);

// Define colors for each class
const classColors: Record<ClassType, string> = {
    '1st': '#fa938e',
    '2nd': '#98bf45',
    '3rd': '#51cbcf',
    Crew: '#d397ff',
};

const classData: ChartDatum[] = classes.map((pClass: ClassType) => {
    const classTotal = titanicData
        .filter((item: TitanicDatum) => item.Class === pClass)
        .reduce((acc: number, item: TitanicDatum) => acc + item.Count, 0);
    return {
        id: pClass,
        label: `${pClass} Income:`,
        value: classTotal,
        percentage: (classTotal / totalCount) * 100,
        color: classColors[pClass],
    };
});

const classSurvivalData: ChartDatum[] = classes.flatMap((pClass: ClassType) => {
    const classTotal = classData.find((d: ChartDatum) => d.id === pClass)!.value ?? 0;
    const baseColor = classColors[pClass];
    return titanicData
        .filter((item: TitanicDatum) => item.Class === pClass)
        .sort((a: TitanicDatum, b: TitanicDatum) => (a.Survived > b.Survived ? 1 : -1))
        .map((item: TitanicDatum) => ({
            id: `${pClass}-${item.Survived}`,
            label: item.Survived,
            value: item.Count,
            percentage: (item.Count / classTotal) * 100,
            color: item.Survived === 'Yes' ? baseColor : `${baseColor}80`, // 80 is 50% opacity for 'No'
        }));
});

const StyledText = styled('text')(({ theme }: { theme: Theme }) => ({
    fill: theme.palette.text.primary,
    textAnchor: 'middle',
    dominantBaseline: 'central',
    fontSize: 20,
}));

const innerRadius = 50;
const middleRadius = 120;

interface PieCenterLabelProps {
    children: React.ReactNode;
}

function PieCenterLabel({ children }: PieCenterLabelProps): React.ReactElement {
    const { width, height, left, top } = useDrawingArea();
    return (
        <StyledText x={left + width / 2} y={top + height / 2}>
            {children}
        </StyledText>
    );
}

const pieChartSeriesDataList: PieSeries[] = [
    {
        innerRadius,
        outerRadius: middleRadius,
        data: classData,
        arcLabel: (item) =>
            `${item.id} (${(item as any).percentage.toFixed(0)}%)`,
        valueFormatter: ({ value }) =>
            `${value} out of ${totalCount} (${((value / totalCount) * 100).toFixed(0)}%)`,
        highlightScope: { fade: 'global', highlight: 'item' },
        highlighted: { additionalRadius: 2 },
        cornerRadius: 3,
    },
    {
        innerRadius: middleRadius,
        outerRadius: middleRadius + 20,
        data: classSurvivalData,
        arcLabel: (item) =>
            `${item.label} (${(item as any).percentage.toFixed(0)}%)`,
        valueFormatter: ({ value }) =>
            `${value} out of ${totalCount} (${((value / totalCount) * 100).toFixed(0)}%)`,
        arcLabelRadius: 160,
        highlightScope: { fade: 'global', highlight: 'item' },
        highlighted: { additionalRadius: 2 },
        cornerRadius: 3,
    },
];

const pieChartSx = {
    [`& .${pieArcLabelClasses.root}`]: {
        fontSize: '12px',
    },
};

export default function TitanicPie(): React.ReactElement {

    return (
        <Box sx={{ width: '100%', textAlign: 'center' }}>
            <Typography variant="h5" gutterBottom>
                {/* Titanic survival statistics */}
                {"Income statistics"}
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'center', height: 400 }}>
                <PieChart
                    series={pieChartSeriesDataList}
                    sx={pieChartSx}
                    hideLegend
                >
                    <PieCenterLabel>{"Income"}</PieCenterLabel>
                </PieChart>
            </Box>
        </Box>
    );
}