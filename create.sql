DROP TABLE IF EXISTS FECData;
CREATE TABLE FECData(cycles TEXT, designation TEXT, designation_full TEXT, treasurer_name TEXT, organization_type TEXT, organization_type_full TEXT, state TEXT, committee_type TEXT, committee_type_full TEXT, expire_date NUMERIC, party TEXT, party_full TEXT, name TEXT, committee_id TEXT, candidate_ids TEXT, first_file_date NUMERIC, last_file_date NUMERIC, last_f1_date NUMERIC);
.mode csv FECData
.import Data/FEC.csv FECData

DROP TABLE IF EXISTS FCCData;
CREATE TABLE FCCData(id TEXT, station_id TEXT, url TEXT, name TEXT);
.mode csv FCCData
.import Data/FCC.csv FCCData