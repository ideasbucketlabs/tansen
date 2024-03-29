import type { Config } from 'tailwindcss'
const defaultTheme = require('tailwindcss/defaultTheme')

export default {
    content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
    theme: {
        extend: {
            minHeight: {
                80: '20rem',
            },
            fontSize: {
                '10xl': '8rem',
            },
            width: {
                '1/7': '14.2857143%',
            },
            colors: {
                green: {
                    DEFAULT: '#25695C',
                    50: '#E5F6F3',
                    100: '#CAECE6',
                    200: '#92D8CB',
                    300: '#5DC6B3',
                    400: '#389E8C',
                    500: '#25695C',
                    600: '#1D5349',
                    700: '#174039',
                    800: '#0F2925',
                    900: '#081714',
                },
            },
        },
        fontFamily: {
            sans: ['Source Sans Pro', ...defaultTheme.fontFamily.sans],
            system: defaultTheme.fontFamily.sans,
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
        // ...
    ],
} satisfies Config
