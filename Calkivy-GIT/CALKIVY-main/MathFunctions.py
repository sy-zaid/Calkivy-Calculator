from kivy.uix.screenmanager import Screen, ScreenManager


class APlusB(Screen):

    # noinspection PyBroadException
    def ab_wholesq(self, equation):
        """
        This function takes an equation in string type and splits from the operator.
        :param equation: taken from self.ids.Text_Input_1.text
        :return: A list is created at the end for each value i.e. a^2,2ab,b^2. This list is
        used as a result in self.ids.answer.text and displayed in application."""

        # Try block if no error occurs in the calculation.
        try:
            # equation=self.ids.Text_Input_1.text
            powers = {0: '\u2070', 1: '\u00b9', 2: '\u00b2', 3: '\u00b3', 4: '\u2074', 5: '\u2075', 6: '\u2076'}
            powers_reversed = {'\u2070': 0, '\u00b9': 1, '\u00b2': 2, '\u00b3': 3, '\u2074': 4, '\u2075': 5,
                               '\u2076': 6}

            # Making translation of superscripts.
            def powers_list(s):
                """
                |Function 1 in Function ab_wholesq|
                :param s: Takes parameter & converts it to string.
                Digits string is translated into Superscript string. s is used to get the
                required translation.
                :return: Returns the translation of s in form of Superscript(one digit only).
                """

                s = str(s)
                superscript = '⁰¹²³⁴⁵⁶⁷⁸⁹'
                digits = '0123456789'
                res = s.maketrans(digits, superscript)
                return str(s.translate(res))

            # Equation List Splitting by operator.
            if len(equation.split('+')) > 1:
                eq_list = equation.split('+')
                op = '+'
            else:
                eq_list = equation.split('-')
                op = '-'
            # Empty lists for numbers,alphabets & their squares.
            numbers, numbers_sq = ['', ''], ['', '']
            alphabets, alphabets_sq = [[], []], [[], []]

            # Separating alphabets and numbers.
            for i in range(len(eq_list)):
                for j in eq_list[i]:
                    if j.isalpha():
                        alphabets[i] += j
                        alphabets_sq[i] += j

                    # Separating numbers and powers.
                    if j.isnumeric() and j not in powers.values():
                        numbers[i] += j
                        numbers_sq[i] += j

            # Separating equation in two parts (for separating variables).
            a, b = eq_list[0], eq_list[1]

            def powers_dict(var):
                """
                |Function 2 in Function ab_wholesq| Creates a dictionary for all the powers
                in a variable.
                :param var: Variable containing power.
                :return: Returns a dictionary containing variables and
                powers as variable's values.
                """
                powers_dictionary = {}

                for char in var:
                    if char.isalpha():
                        x = char

                    if char.isalpha() and char not in powers.values():
                        powers_dictionary[x] = [char, '1']

                    if char in powers_reversed.keys():
                        powers_dictionary[x] = [char, str(powers_reversed[char])]

                return powers_dictionary

            # Two separate dictionaries for a & b parts of equation.
            pw_in_a = powers_dict(a)
            pw_in_b = powers_dict(b)

            # Variables for two parts and their squares.
            var_a_sq, var_b_sq, var_a, var_b = '', '', '', ''

            # Loops for squaring the powers of variables(Basically multiplying the powers with 2).
            for key, val in pw_in_a.items():
                var_a_sq += key + powers_list(int(val[1]) * 2)
                var_a += key + powers_list(val[1])

            for key, val in pw_in_b.items():
                var_b_sq += key + powers_list(int(val[1]) * 2)
                var_b += key + powers_list(val[1])

            # Square of numbers.
            # If there are constants in equation.
            for i in range(len(numbers)):
                if numbers[i] != '':
                    numbers_sq[i] = str(int(numbers_sq[i]) ** 2)

                # Else, no constants in equation.
                else:
                    numbers_sq[i] = '1'
                    numbers[i] = '1'

            res_list = (numbers_sq[0] + var_a_sq, str(int(numbers[0]) * int(numbers[1]) * 2) + var_a + var_b,
                        numbers_sq[1] + var_b_sq)

            result = str(res_list[0] + op + res_list[1] + op + res_list[2])
            self.ids.answer.text = result

        # Exception is raised if any error occurs in calculation.This must be due to the syntax of equation.
        except Exception:
            self.ids.answer.text = 'Syntax Error!'

    # noinspection PyBroadException
    def power_buttons(self, obj):
        """
        Adds up a power value depending upon the button pressed.
        :param obj: button id.
        :return: Text_Input with power
        """
        try:
            if self.ids.square.text == obj:
                self.ids.Text_Input_1.text = self.ids.Text_Input_1.text + '\u00b2'
            if self.ids.cube.text == obj:
                self.ids.Text_Input_1.text = self.ids.Text_Input_1.text + '\u00b3'
            if self.ids.clr.text == obj:
                self.ids.Text_Input_1.text = ''

        except Exception:
            self.ids.Text_Input_1.text += ''

    # noinspection PyBroadException
    def update(self):
        powers = {0: '\u2070', 1: '\u00b9', 2: '\u00b2',
                  3: '\u00b3', 4: '\u2074', 5: '\u2075', 6: '\u2076', 7: '\u2077'}
        try:
            for i in self.ids.Text_Input_1.text:

                if i == '^':
                    ndx = self.ids.Text_Input_1.text.index(i)
                    x = ndx + 1
                    var = powers[int(self.ids.Text_Input_1.text[x])]
                    i = i + self.ids.Text_Input_1.text[x]

                    self.ids.Text_Input_1.text = self.ids.Text_Input_1.text.replace(i, var)

                else:
                    continue
        except Exception:
            self.ids.Text_Input_1.text = 'Incorrect power.'


class Factorial(Screen):

    def clr(self, obj):
        obj.text = ''

    # noinspection PyBroadException
    def factorial(self, number, out):

        res = 1
        sol = ''
        count = 0
        try:
            for j in range(1, int(number.text) + 1):
                count += 1
                sol += 'x' + str(j)[::-1]
                res *= j

            s = str(sol)[::-1]
            sol = s[:-1]

            if count > 10:
                sol = sol[0:8] + '...' + sol[-6:]
                res = "{:e}".format(res)
            else:
                res = str(res)
            out.text = sol + '\nFactorial: ' + res

        except OverflowError:
            number.text = 'Infinity'

        except Exception:
            number.text = 'Incorrect Value'


class GCD(Screen):
    def clr(self, obj):
        obj.text = ''

    # noinspection PyBroadException
    def gcd(self, numbers, out):
        try:
            numbers_list = numbers.text.split(',')

        except Exception:
            numbers.text = 'Syntax Error!'

        number1 = int(numbers_list[0])
        number2 = int(numbers_list[-1])

        if number2 > number1:
            number1, number2 = number2, number1

        for i in range(number2):
            if number2 != 0:
                res = number1 % number2
                number1 = number2
                number2 = res
            else:
                out.text = 'GCD: '+ str(number1)



