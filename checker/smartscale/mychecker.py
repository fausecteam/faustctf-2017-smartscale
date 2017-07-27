#!/usr/bin/env python3

import os
import json
import random
import telnetlib
import time
import subprocess
import re
import string
import socket

CHECKER_DIR = os.path.dirname(os.path.realpath(__file__))
from ctf_gameserver.checker import BaseChecker
from ctf_gameserver.checker.constants import OK, NOTWORKING, NOTFOUND, TIMEOUT


class SmartScaleChecker(BaseChecker):

    def check_flag(self, tick):
        try:
            flag_id = self.retrieve_blob('flag_id_' + str(tick))
            if not flag_id:
                return NOTFOUND
            flag_id = flag_id.decode()
            if len(flag_id) > 0:
                s = telnetlib.Telnet(self._ip, 31337, timeout=10)

                data = {'action': 'retrieve', 'flag_id': flag_id}
                data_json = json.dumps(data) + '\n'

                self.logger.debug("sending data to service")
                s.write(data_json.encode())

                self.logger.debug("receiving data from service")
                result_str = s.read_all().decode()

                result = json.loads(result_str)
                if 'status' in result and result['status'] == 'ok' and 'data' in result:
                    data = result['data']
                    if 'comment' in data and data['comment'] == self.get_flag(tick) and 'hash' in data:
                        # Flag is ok => check Hash
                        hash = data['hash']

                        hash_str = re.search(r"data\":(\{.*\})\}$", result_str).group(1)[:-43] + '}\n'
                        proc = subprocess.Popen('java -cp {}/AwesomeHash.jar \
                                ninja.faust.smartscale.AwesomeHash'.format(CHECKER_DIR), \
                                shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE)

                        proc.stdin.write(hash_str.encode())
                        proc.stdin.close()
                        orighash = proc.stdout.readline().decode()[:-1]
                        r = proc.wait()

                        self.logger.debug("java returned with %s", r)
                        if hash == orighash:
                            s.close()
                            return OK
                        else:
                            self.logger.debug("hashes do not match")
                            s.close()
                            return NOTWORKING

                self.logger.debug("receive within check_flag did not receive proper data")
                s.close()
                return NOTFOUND
        except ValueError:
            self.logger.debug("JSON decode error %s", result_str)
        except socket.timeout as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except ConnectionResetError as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except AttributeError as e:
            self.logger.exception("%s", e)
            return NOTWORKING
        except BaseException as e:
            self.logger.exception("%s", e)
            return NOTWORKING

        s.close()
        return NOTFOUND

    def place_flag(self):
        try:
            s = telnetlib.Telnet(self._ip, 31337, timeout=10)

            data = {'action': 'store', 'data': {}}
            data['data']['weight'] = round(random.random() * 60 + 50, 2)
            data['data']['size'] = round(random.random() * 0.7 + 1.5, 2)
            data['data']['fat_quotient'] = round(random.random() * 0.5, 2)
            data['data']['comment'] = self.get_flag(self.tick)
            data_json = json.dumps(data) + '\n'

            self.logger.debug("sending data to service")
            s.write(data_json.encode())

            self.logger.debug("receiving data from service")
            result = s.read_all().decode()

            result = json.loads(result)
            if 'status' in result and result['status'] == 'ok' and 'flag_id' in result:
                self.store_blob("flag_id_" + str(self.tick), result['flag_id'].encode())
                s.close()
                return OK
        except ValueError:
            self.logger.debug("JSON decode error %s", result)
        except socket.timeout as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except ConnectionResetError as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except AttributeError as e:
            self.logger.exception("%s", e)
            return NOTWORKING
        except BaseException as e:
            self.logger.exception("%s", e)
            return NOTWORKING

        self.logger.debug("receive within place_flag did not return proper data")
        s.close()
        return NOTWORKING

    def check_service(self):
        try:
            s = telnetlib.Telnet(self._ip, 31337, timeout=10)

            idata = {'action': 'store', 'data': {}}
            idata['data']['weight'] = round(random.random() * 60 + 50, 2)
            idata['data']['size'] = round(random.random() * 0.7 + 1.5, 2)
            idata['data']['fat_quotient'] = round(random.random() * 0.5, 2)
            letters = list(string.printable)
            random.shuffle(letters)
            letters = "".join(letters)
            idata['data']['comment'] = letters[:random.randint(2, len(letters))]
            idata_json = json.dumps(idata) + '\n'

            s.write(idata_json.encode())

            result = s.read_all().decode()

            result = json.loads(result)
            if 'status' in result and result['status'] == 'ok' and 'flag_id' in result:
                status = OK

            s.close()
        except ValueError:
            self.logger.debug("JSON decode error %s", result)
        except socket.timeout as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except ConnectionResetError as e:
            self.logger.exception("%s", e)
            return TIMEOUT
        except AttributeError as e:
            self.logger.exception("%s", e)
            return NOTWORKING
        except BaseException as e:
            self.logger.exception("%s", e)
            return NOTWORKING


        if status == OK:
            time.sleep(random.random()) 
            try:
                s = telnetlib.Telnet(self._ip, 31337, timeout=10)

                tmpdata = {'action': 'retrieve', 'flag_id': result['flag_id']}
                tmpdata_json = json.dumps(tmpdata) + '\n'

                s.write(tmpdata_json.encode())

                result_str = s.read_all().decode()

                result = json.loads(result_str)
                print(result)
                if 'status' in result and result['status'] == 'ok' and 'data' in result:
                    odata = result['data']
                    if 'comment' in odata and odata['comment'] == idata['data']['comment'] \
                            and 'hash' in odata:
                        # Flag is ok => check Hash
                        hash = odata['hash']

                        hash_str = re.search(r"data\":(\{.*\})\}$", result_str).group(1)[:-43] + '}\n'
                        proc = subprocess.Popen('java -cp {}/AwesomeHash.jar \
                                ninja.faust.smartscale.AwesomeHash'.format(CHECKER_DIR), \
                                shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE)

                        proc.stdin.write(hash_str.encode())
                        proc.stdin.close()
                        orighash = proc.stdout.readline().decode()[:-1]

                        proc.wait()
                        if hash == orighash:
                            s.close()
                            return OK
                        else:
                            s.close()
                            return NOTWORKING

                s.close()
                return NOTFOUND
            except ValueError:
                self.logger.debug("JSON decode error %s", result_str)
            except socket.timeout as e:
                self.logger.exception()
                return TIMEOUT
            except ConnectionResetError as e:
                self.logger.exception()
                return TIMEOUT
            except AttributeError as e:
                self.logger.exception()
                return NOTWORKING
            except BaseException as e:
                self.logger.exception("%s", e)
                return NOTWORKING

        s.close()
        return NOTFOUND


if __name__ == '__main__':
    import time
    tick = time.time() % 11000
    checker = SmartScaleChecker(tick, 1, 1, '127.0.0.1')
    print("Placing flag...", "OK" if checker.place_flag() else "FAILED")
    print("Checking flag...", "OK" if checker.check_flag(tick) else "FAILED")
