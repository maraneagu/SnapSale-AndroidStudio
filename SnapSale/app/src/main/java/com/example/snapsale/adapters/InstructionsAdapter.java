package com.example.snapsale.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapsale.R;
import com.example.snapsale.network.requests.TranslateRequest;

import java.util.List;


// HELPFUL LINKS:
// 1). "RecyclerView in Android Studio-Tutorial 2021", CodesKing, URL: https://www.youtube.com/watch?v=ppEw_nzCgO4&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=61&t=567s&ab_channel=CodesKing


public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {
    private final Activity activity;
    private final List<String> instructions;
    private final String targetLanguage, sourceLanguage;

    public InstructionsAdapter(Activity activity, List<String> instructions, String targetLanguage, String sourceLanguage) {
        this.activity = activity;
        this.instructions = instructions;

        this.targetLanguage = targetLanguage;
        this.sourceLanguage = sourceLanguage;
    }

    @NonNull
    @Override
    public InstructionsAdapter.InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsAdapter.InstructionViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_store_recipe_instructions_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsAdapter.InstructionViewHolder holder, int position) {
        holder.instruction.setVisibility(View.GONE);

        if (targetLanguage.equals("en")) {
            holder.instruction.setText(instructions.get(position));
            holder.instruction.setVisibility(View.VISIBLE);
        }
        else {
            TranslateRequest.translate(activity, targetLanguage, sourceLanguage, instructions.get(position),
                    translatedText -> { holder.instruction.setText(translatedText);
                        holder.instruction.setVisibility(View.VISIBLE);});
        }
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public static class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView instruction;

        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);

            instruction = itemView.findViewById(R.id.favorites_store_recipe_instruction);
        }
    }
}

