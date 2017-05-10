// R’lyeh
#include <inttypes.h>
#include <stdio.h>

int64_t carvedToWritten(int64_t n)
{
    int64_t r = 0;
    for (int64_t i = 0; i < 64; ++i)
    {
        int64_t a = 0;
        for (int64_t j = n; j >= 0; --j)
        {
            int64_t b = 0;
            for (int64_t k = 0; k <= i; ++k)
            {
                int64_t c = a ^ ((i & (n ^ j) & 1) << k);
                a ^= (j & (1LL << k)) ^ b;
                b = (((c & j) | ((c ^ j) & b)) & (1LL << k)) << 1;
            }
        }
        r |= (a & (1LL << i));
    }
    return r;
}

int64_t carvedToWrittenFast(int64_t n)
{
    int64_t r = 0;
    for (int64_t i = 0; i < 64; ++i)
    {
        int64_t a = 0;
        if (i % 2 == 0) {
            a = n * (n + 1) / 2;
        } else {
            a = (n + 1) / 2;
        }
        r |= (a & (1LL << i));
    }
    return r;
}

uint32_t writtenToCarved(int64_t r) {
    
    uint32_t m = (r & 0xAAAAAAAA) << 1;
    for (int i = 0; i < 0xFFFF; i++) {
        uint32_t n = m;
        for (int k = 0; k < 16; k++) {
            n |= (i & (1 << k)) << (k + 1);
        }
        if (carvedToWrittenFast(n - 1) == r) {
            return n - 1;
        } else if (carvedToWrittenFast(n) == r) {
            return n;
        }
    }
    return 0;
}

int main() {
    int c;
    scanf("%d", &c);

    for (int i = 1; i <= c; i++) {
        int64_t writtenNumber;
        scanf("%" SCNu64, &writtenNumber);

        uint32_t carvedNumber = writtenToCarved(writtenNumber);
        if (carvedNumber == 0) {
            printf("Case #%d: IMPOSSIBLE\n", i);
        } else {
            printf("Case #%d: %" PRIu32 "\n", i, carvedNumber);
        }
    }
    return 0;
}
