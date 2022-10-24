import requests

import time
import hashlib
import random


def ds(url, data=''):
    salt = 'xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs'
    (q := url.split('?')[1].split('&')).sort()
    q = '&'.join(q)
    r = random_string(6)
    t = int(time.time())
    chk = f'salt={salt}&t={t}r={r}&b={data}&q={q}'
    chk = hashlib.md5(chk.encode()).hexdigest()
    return f'{t},{r},{chk}'


def random_string(length):
    chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
    return ''.join(random.sample(chars, length))


def get_rpc_headers(s: requests.session):
    headers = {'X-Rpc-Channel':'miyousheluodi',
               'X-Rpc-Device_id':,
               'X-Rpc-Client_type':,
               'X-Rpc-App_version':,
               'X-Rpc-Sys_version':,
               'X-Rpc-Device_name':',
               'X-Rpc-Device_model':''
               }
    return


def main():
    s = requests.session()
    s.headers


if __name__ == '__main__':
    main()
