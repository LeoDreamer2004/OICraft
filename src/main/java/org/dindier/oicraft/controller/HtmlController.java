package org.dindier.oicraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HtmlController {
    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("user", "Dindier");
        return "index";
    }

    @GetMapping("/problem")
    public String problem(Model model) {

        // Just a temp test page

        class Sample {
            public final String input;
            public final String output;

            public Sample(String input, String output) {
                this.input = input;
                this.output = output;
            }
        }

        class Problem {
            public final int id;
            public final String title;
            public final String description;
            public final String inputFormat;
            public final String outputFormat;
            public final String difficulty;
            public final List<Sample> samples;

            public Problem(int id, String title, String description, String inputFormat, String outputFormat, String difficulty, List<Sample> samples) {
                this.id = id;
                this.title = title;
                this.inputFormat = inputFormat;
                this.outputFormat = outputFormat;
                this.description = description;
                this.difficulty = difficulty;
                this.samples = samples;
            }
        }

        List<Sample> samples = new ArrayList<>();
        samples.add(new Sample("1 2", "3"));
        samples.add(new Sample("3 4", "7"));

        Problem problem = new Problem(1, "a+b 问题", "把两个数相加",
                "两个整数 $a$, $b$，以空格隔开", "一个整数 $n = a + b$" , "easy", samples);
        model.addAttribute("problem", problem);

        return "problem";
    }
}
