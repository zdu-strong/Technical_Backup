import { observer } from "mobx-react-use-autorun";
import { Box, Chip, Typography } from "@mui/material";
import Income from "@component/CountryDashboard/Income";
import Outlay from "@component/CountryDashboard/Outlay";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartShopping, faCircleDollarToSlot, faHouseChimney } from '@fortawesome/free-solid-svg-icons'
import { LineChart } from "@mui/x-charts";

const lineChartAxisDataList = [{ data: [1, 2, 3, 5, 8, 10] }];
const lineChartSeriesDataList = [
    {
        data: [2, 5.5, 2, 8.5, 1.5, 5],
        area: true,
    },
];

export default observer(() => {

    return <div className="flex flex-col">
        <div className="flex flex-row justify-start">
            <Box
                sx={{
                    width: '50%',
                    overflow: 'auto',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h6">
                    {/* {"National Reserves comparison (2024 vs 2010-19 Avg)"} */}
                    {"National Reserves"}
                </Typography>
                <LineChart
                    xAxis={lineChartAxisDataList}
                    series={lineChartSeriesDataList}
                    height={300}
                />
            </Box>
            <div className="flex flex-col justify-center" >
                <Chip icon={<FontAwesomeIcon icon={faCartShopping} />} label="Consumption tax: 10%" />
                <Chip icon={<FontAwesomeIcon icon={faCircleDollarToSlot} />} label="Commodity profit tax: 40%" style={{ marginTop: "1em" }} />
                <Chip icon={<FontAwesomeIcon icon={faHouseChimney} />} label="Fixed asset tax: 2%" style={{ marginTop: "1em" }} />
                <Chip icon={<FontAwesomeIcon icon={faHouseChimney} />} label="National Reserves: $1000" style={{ marginTop: "1em" }} />
                <Chip icon={<FontAwesomeIcon icon={faHouseChimney} />} label="Basic living allowance: $100/day" style={{ marginTop: "1em" }} />
                <Chip icon={<FontAwesomeIcon icon={faHouseChimney} />} label="Sovereign Dividend: $100/day" style={{ marginTop: "1em" }} />
            </div>
        </div>
        <div className="flex flex-row" style={{ width: "1000px" }}>
            <Income />
            <Outlay />
        </div>
    </div>
})